package lyng.decode

import chisel3._
import chisel3.util._
import lyng.ControlUnitSig
import lyng.RefernceVals

class ControlUnit extends Module {
    val io = IO(new Bundle {
        val opcode = Input(UInt(5.W))
        val func = Input(UInt(2.W))
        val control = Output(new ControlUnitSig)
    })

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
    val is_5b_unsigned = isInstr("ADDI") | isInstr("SUBI")
    val is_5b_signed = isInstr("LDIDR") | isInstr("STIDR") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JCO")
    val is_8b_unsigned = isInstr("MVIH") | isInstr("MVIL")
    when (is_5b_unsigned) {
        io.control.ext_mode := "b000".U
    } .elsewhen (is_5b_signed) {
        io.control.ext_mode := "b001".U
    } .elsewhen (is_8b_unsigned) {
        io.control.ext_mode := "b010".U
    } otherwise { // KMP, JCO, JAL
        io.control.ext_mode := "b101".U
    }

    // alu_src
    // source 2 is register   --->  0
    // source 2 is immedeate  --->  1
    val is_src_imm = isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("JMPI") | isInstr("SUBI") | isInstr("SHFL") | isInstr("SHFA") | isInstr("MVIL") | isInstr("MVIH")
    when (is_src_imm) {
        io.control.alu_src := 1.U
    } otherwise {
        io.control.alu_src := 0.U
    }

    // alu_op
    val is_add_group = isInstr("ADD") | isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("LDIDX") | isInstr("STIDX") | isInstr("JMPI")
    val is_sub_group = isInstr("SUB") | isInstr("SUBI") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JEO")
    when (is_add_group) {
        io.control.alu_op := "b0010".U
    } .elsewhen (isInstr("ADC")) {
        io.control.alu_op := "b0011".U
    } .elsewhen (is_sub_group) {
        io.control.alu_op := "b0100".U
    } .elsewhen (isInstr("SBB")) {
        io.control.alu_op := "b0101".U
    } .elsewhen (isInstr("AND")) {
        io.control.alu_op := "b1000".U
    } .elsewhen (isInstr("OR")) {
        io.control.alu_op := "b1001".U
    } .elsewhen (isInstr("XOR")) {
        io.control.alu_op := "b1010".U
    } .elsewhen (isInstr("NOT")) {
        io.control.alu_op := "b1011".U
    } .elsewhen (isInstr("SHFL")) {
        io.control.alu_op := "b1100".U
    } .elsewhen (isInstr("SHFA")) {
        io.control.alu_op := "b1101".U
    } .elsewhen (isInstr("MVIH")) {
        io.control.alu_op := "b1110".U
    } .elsewhen (isInstr("MVIL")) {
        io.control.alu_op := "b1111".U
    } otherwise { // NOP
        io.control.alu_op := 0.U
    }

    // carry_write
    when (isInstr("ADC") | isInstr("SBB") | isInstr("ADDI") | isInstr("SUBI") | isInstr("STC")) {
        io.control.carry_write := 1.U
    } otherwise {
        io.control.carry_write := 0.U
    }

    // jmp_mode
    // Rs >= Rd     --->  010
    // Rs <= Rd     --->  011
    // carry == 1   --->  100
    // Rs == Rd     --->  101
    // always jump  --->  111
    // never jump   --->  000
    when (isInstr("JGEO")) {
        io.control.jmp_mode := "b010".U
    } .elsewhen (isInstr("JLEO")) {
        io.control.jmp_mode := "b011".U
    } .elsewhen (isInstr("JCO")) {
        io.control.jmp_mode := "b100".U
    } .elsewhen (isInstr("JEO")) {
        io.control.jmp_mode := "b101".U
    } .elsewhen (isInstr("JMP") | isInstr("JMPI") | isInstr("CALL") | isInstr("JAL") | isInstr("RET")) {
        io.control.jmp_mode := "b111".U
    } otherwise {
        io.control.jmp_mode := 0.U
    }

    // stack_op
    when (isInstr("POP") | isInstr("RET")) {
        io.control.stack_op := "b11".U
    } .elsewhen (isInstr("PUSH") | isInstr("CALL") | isInstr("JAL")) {
        io.control.stack_op := "b10".U
    } .elsewhen (isInstr("MOVSP")) {
        io.control.stack_op := "b01".U
    } otherwise {
        io.control.stack_op := 0.U
    }

    // mem_data_src
    // source is Rd  --->  0
    // source is PC  --->  1
    when (isInstr("JAL") | isInstr("CALL")) {
        io.control.mem_data_src := 1.U
    } otherwise {
        io.control.mem_data_src := 0.U
    }

    // mem_addr_src
    // source is ALU  --->  1
    // source is SP   --->  0
    when (isInstr("LDIDR") | isInstr("STIDR") | isInstr("LDIDX") | isInstr("STIDX")) {
        io.control.mem_addr_src := 0.U
    } otherwise {
        io.control.mem_addr_src := 1.U
    }

    // mem_write
    when (isInstr("LDIDR") | isInstr("LDIDX") | isInstr("POP") | isInstr("RET")) {
        io.control.mem_write := 1.U
    } otherwise {
        io.control.mem_write := 0.U
    }

    // mem_read
    when (isInstr("STIDR") | isInstr("STIDX") | isInstr("PUSH") | isInstr("CALL") | isInstr("JAL")) {
        io.control.mem_read := 1.U
    } otherwise {
        io.control.mem_read := 0.U
    }

    // rd_src
    // source is ALU result  --->  0
    // source is memory      --->  1
    when (isInstr("LDIDR") | isInstr("LDIDX") | isInstr("POP")) {
        io.control.rd_src := 1.U
    } otherwise {
        io.control.rd_src := 0.U
    }

    io.control.reg_write := 0.U
    io.control.carry_src := 0.U
    io.control.jmp_amt_src := 0.U
    io.control.pc_src := 0.U
}

object ControlUnitMain extends App {
    println("Generating ALU")
    (new chisel3.stage.ChiselStage).emitVerilog(new ControlUnit(), Array("--target-dir", "generated"))
}
