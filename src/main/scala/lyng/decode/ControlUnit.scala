package lyng.decode

import chisel3._
import chisel3.util._
import lyng.ControlUnitSig
import lyng.RefernceVals

class ControlUnit extends Module {
    val io = IO(new Bundle {
        val opcode = Input(UInt(5.W))
        val func = Input(UInt(2.W))
        val ctrl = Output(new ControlUnitSig)
    })

    io.ctrl := 0.U.asTypeOf(new ControlUnitSig)

    // helper function to detect instruction based on opcode and func code
    def isInstr(opcodeStr : String) = {
        Mux(io.opcode <= "b00010".U, io.opcode === RefernceVals.op_ref(opcodeStr) & io.func === RefernceVals.func_ref(opcodeStr), io.opcode === RefernceVals.op_ref(opcodeStr))
    }



    // ext_mode
    // 5-bit unsigned   --->  000
    // 5-bit signed     --->  001
    // 8-bit unsigned   --->  010
    // 8-bit signed     --->  011
    // 11-bit unsigned  --->  100
    // 11-bit signed    --->  101
    val is_5b_unsigned = Wire(UInt(1.W))
    val is_5b_signed = Wire(UInt(1.W))
    val is_8b_unsigned = Wire(UInt(1.W))
    is_5b_unsigned := isInstr("ADDI") | isInstr("SUBI")
    is_5b_signed := isInstr("LDIDR") | isInstr("STIDR") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JEO") | isInstr("SHFL") | isInstr("SHFA")
    is_8b_unsigned := isInstr("MVIH") | isInstr("MVIL")

    when (is_5b_unsigned === 1.U) {
        io.ctrl.ext_mode := "b000".U
    } .elsewhen (is_5b_signed === 1.U) {
        io.ctrl.ext_mode := "b001".U
    } .elsewhen (is_8b_unsigned === 1.U) {
        io.ctrl.ext_mode := "b010".U
    } otherwise { // KMP, JCO, JAL
        io.ctrl.ext_mode := "b101".U
    }

    // alu_src
    // source 2 is register   --->  0
    // source 2 is immedeate  --->  1
    val is_src_imm = Wire(UInt(1.W))
    is_src_imm := isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("JMPI") | isInstr("SUBI") | isInstr("SHFL") | isInstr("SHFA") | isInstr("MVIL") | isInstr("MVIH")
    when (is_src_imm === 1.U) {
        io.ctrl.alu_src := 1.U
    } otherwise {
        io.ctrl.alu_src := 0.U
    }

    // alu_op
    val is_add_group = Wire(UInt(1.W))
    val is_sub_group = Wire(UInt(1.W))
    is_add_group := isInstr("ADD") | isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("LDIDX") | isInstr("STIDX") | isInstr("JMPI")
    is_sub_group := isInstr("SUB") | isInstr("SUBI") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JEO")
    when (is_add_group === 1.U) {
        io.ctrl.alu_op := "b0010".U
    } .elsewhen (isInstr("ADC")) {
        io.ctrl.alu_op := "b0011".U
    } .elsewhen (is_sub_group === 1.U) {
        io.ctrl.alu_op := "b0100".U
    } .elsewhen (isInstr("SBB")) {
        io.ctrl.alu_op := "b0101".U
    } .elsewhen (isInstr("AND")) {
        io.ctrl.alu_op := "b1000".U
    } .elsewhen (isInstr("OR")) {
        io.ctrl.alu_op := "b1001".U
    } .elsewhen (isInstr("XOR")) {
        io.ctrl.alu_op := "b1010".U
    } .elsewhen (isInstr("NOT")) {
        io.ctrl.alu_op := "b1011".U
    } .elsewhen (isInstr("SHFL")) {
        io.ctrl.alu_op := "b1100".U
    } .elsewhen (isInstr("SHFA")) {
        io.ctrl.alu_op := "b1101".U
    } .elsewhen (isInstr("MVIH")) {
        io.ctrl.alu_op := "b1110".U
    } .elsewhen (isInstr("MVIL")) {
        io.ctrl.alu_op := "b1111".U
    } .elsewhen (isInstr("STC")) {
        io.ctrl.alu_op := "b0001".U
    } otherwise { // NOP
        io.ctrl.alu_op := 0.U
    }

    // carry_write
    when (isInstr("ADC") | isInstr("SBB") | isInstr("ADDI") | isInstr("SUBI") | isInstr("STC") | isInstr("ADD") | isInstr("SUB")) {
        io.ctrl.carry_write := 1.U
    } otherwise {
        io.ctrl.carry_write := 0.U
    }

    // jmp_mode
    // Rs >= Rd     --->  010
    // Rs <= Rd     --->  011
    // carry == 1   --->  100
    // Rs == Rd     --->  101
    // always jump  --->  111
    // never jump   --->  000
    when (isInstr("JGEO")) {
        io.ctrl.jmp_mode := "b010".U
    } .elsewhen (isInstr("JLEO")) {
        io.ctrl.jmp_mode := "b011".U
    } .elsewhen (isInstr("JCO")) {
        io.ctrl.jmp_mode := "b100".U
    } .elsewhen (isInstr("JEO")) {
        io.ctrl.jmp_mode := "b101".U
    } .elsewhen (isInstr("JMP") | isInstr("JMPI") | isInstr("CALL") | isInstr("JAL") | isInstr("RET")) {
        io.ctrl.jmp_mode := "b111".U
    } otherwise {
        io.ctrl.jmp_mode := 0.U
    }

    // stack_op
    when (isInstr("POP") | isInstr("RET")) {
        io.ctrl.stack_op := "b11".U
    } .elsewhen (isInstr("PUSH") | isInstr("CALL") | isInstr("JAL")) {
        io.ctrl.stack_op := "b10".U
    } .elsewhen (isInstr("MOVSP")) {
        io.ctrl.stack_op := "b01".U
    } otherwise {
        io.ctrl.stack_op := 0.U
    }

    // mem_data_src
    // source is Rd  --->  0
    // source is PC  --->  1
    when (isInstr("JAL") | isInstr("CALL")) {
        io.ctrl.mem_data_src := 1.U
    } otherwise {
        io.ctrl.mem_data_src := 0.U
    }

    // mem_addr_src
    // source is ALU  --->  1
    // source is SP   --->  0
    when (isInstr("LDIDR") | isInstr("STIDR") | isInstr("LDIDX") | isInstr("STIDX")) {
        io.ctrl.mem_addr_src := 0.U
    } otherwise {
        io.ctrl.mem_addr_src := 1.U
    }

    // mem_write
    when (isInstr("LDIDR") | isInstr("LDIDX") | isInstr("POP") | isInstr("RET")) {
        io.ctrl.mem_read := 1.U
    } otherwise {
        io.ctrl.mem_read := 0.U
    }

    // mem_read
    when (isInstr("STIDR") | isInstr("STIDX") | isInstr("PUSH") | isInstr("CALL") | isInstr("JAL")) {
        io.ctrl.mem_write := 1.U
    } otherwise {
        io.ctrl.mem_write := 0.U
    }

    // rd_src
    // source is ALU result  --->  0
    // source is memory      --->  1
    when (isInstr("LDIDR") | isInstr("LDIDX") | isInstr("POP")) {
        io.ctrl.rd_src := 1.U
    } otherwise {
        io.ctrl.rd_src := 0.U
    }

    // reg_write
    val is_reg_wr = Wire(UInt(1.W))
    is_reg_wr := isInstr("ADD") | isInstr("ADC") | isInstr("SUB") | isInstr("SBB") | isInstr("AND") | isInstr("OR") | isInstr("XOR") | isInstr("NOT") | isInstr("SHFL") | isInstr("SHFA") | isInstr("ADDI") | isInstr("SUBI") | isInstr("MVIH") | isInstr("MVIL") | isInstr("LDIDR") | isInstr("LDIDX") | isInstr("POP")
    when (is_reg_wr === 1.U) {
        io.ctrl.reg_write := 1.U
    } otherwise {
        io.ctrl.reg_write := 0.U
    }

    // carry_src
    when (isInstr("STC")) {
        io.ctrl.carry_src := 1.U
    } otherwise {
        io.ctrl.carry_src := 0.U
    }

    when (isInstr("JGEO") | isInstr("JLEO") | isInstr("JEO")) {
        io.ctrl.rd_addr_src := 1.U
    } otherwise {
        io.ctrl.rd_addr_src := 0.U
    }

    /**
    // jmp_amt_src
    when (isInstr("CALL") | isInstr("JAL") | isInstr("JMP") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JCO") | isInstr("JEO")) {
        io.ctrl.jmp_amt_src := 1.U
    } otherwise {
        io.ctrl.jmp_amt_src := 0.U
    }*/

    // pc_src
    when (isInstr("JMP") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JCO") | isInstr("JEO")) {
        io.ctrl.pc_src := "b01".U
    } .elsewhen (isInstr("CALL") | isInstr("JAL") | isInstr("JMPI")) {
        io.ctrl.pc_src := "b10".U
    } . elsewhen (isInstr("RET")) {
        io.ctrl.pc_src := "b11".U
    } otherwise {
        io.ctrl.pc_src := 0.U
    }
}

/*
object ControlUnitMain extends App {
    println("Generating ALU")
    (new chisel3.stage.ChiselStage).emitVerilog(new ControlUnit(), Array("--target-dir", "generated"))
}*/
