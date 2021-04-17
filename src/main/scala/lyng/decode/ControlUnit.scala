package lyng.decode

import chisel3._
import chisel3.util._
import scala.collection.immutable.ListMap

class ControlUnitSig extends Bundle {
    val ext_mode = UInt(3.W) // sign-extention module mode
    val alu_src = UInt(1.W)
    val alu_op = UInt(4.W)
    val carry_write = UInt(1.W)
    val jmp_mode = UInt(3.W)
    val stack_op = UInt(2.W)
    val mem_data_src = UInt(1.W)
    val mem_addr_src = UInt(1.W)
    val mem_read = UInt(1.W)
    val mem_write = UInt(1.W)
    val rd_src = UInt(1.W)
}

class ControlUnit extends Module {
    val io = IO(new Bundle {
        val opcode = Input(UInt(5.W))
        val func = Input(UInt(2.W))
        val control = Output(new ControlUnitSig)
    })

    val op_ref = ListMap("ADD"   -> "b00000".U,
                        "ADC"   -> "b00000".U,
                        "SUB"   -> "b00000".U,
                        "SBB"   -> "b00000".U,
                        "AND"   -> "b00001".U,
                        "OR"    -> "b00001".U,
                        "XOR"   -> "b00001".U,
                        "NOT"   -> "b00001".U,
                        "SHFL"  -> "b00010".U,
                        "SHFA"  -> "b00010".U,
                        "ADDI"  -> "b00011".U,
                        "SUBI"  -> "b00100".U,
                        "MVIH"  -> "b00101".U,
                        "MVIL"  -> "b00110".U,
                        "LDIDR" -> "b00111".U,
                        "STIDR" -> "b01000".U,
                        "LDIDX" -> "b01001".U,
                        "STIDX" -> "b01010".U,
                        "JMP"   -> "b01011".U,
                        "JMPI"  -> "b01100".U,
                        "JMPI"  -> "b01100".U,
                        "JGEO"  -> "b01101".U,
                        "JLEO"  -> "b01110".U,
                        "JCO"   -> "b01111".U,
                        "JEO"   -> "b10000".U,
                        "PUSH"  -> "b10001".U,
                        "POP"   -> "b10010".U,
                        "CALL"  -> "b10011".U,
                        "JAL"   -> "b10100".U,
                        "MOVSP" -> "b10101".U,
                        "RET"   -> "b10110".U,
                        "STC"   -> "b10111".U
    )

    val func_ref = ListMap("ADD"   -> "b00".U,
                           "ADC"   -> "b01".U,
                           "SUB"   -> "b10".U,
                           "SBB"   -> "b11".U,
                           "AND"   -> "b00".U,
                           "OR"    -> "b01".U,
                           "XOR"   -> "b10".U,
                           "NOT"   -> "b11".U,
                           "SHFL"  -> "b00".U,
                           "SHFA"  -> "b01".U

    )

    // helper function to detect instruction based on opcode and func code
    def isInstr(opcodeStr : String) = {
        Mux(io.opcode <= "b00010".U,
            io.opcode === op_ref(opcodeStr) & io.func === func_ref(opcodeStr),
            io.opcode === op_ref(opcodeStr))
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
    } othervise { // KMP, JCO, JAL
        io.control.ext_mode := "b101".U
    }

    // alu_src
    // source 2 is register   --->  0
    // source 2 is immedeate  --->  1
    val is_src_imm = isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("JMPI") | isInstr("SUBI") | isInstr("SHIFL") | isInstr("SHIFTA") | isInstr("MVIL") | isInstr("MVIH")
    when (is_src_imm) {
        io.control.alu_src := 1.U
    } othervise {
        io.control.alu_src := 0.U
    }

    // alu_op
    val is_add_group = isInstr("ADD") | isInstr("ADDI") | isInstr("LDIDR") | isInstr("STIDR") | isInstr("LDIDR") | isInstr("STDIX") | isInstr("JMPI")
    val is_sub_group = isInstr("SUB") | isInstr("SUBI") | isInstr("JGEO") | isInstr("JLEO") | isInstr("JEO")
    when (is_add_group) {
        io.control.alu_op := "b0010".U
    } .elsewhen (isInstr("ADC")) {
        io.control.ext_mode := "b0011".U
    } .elsewhen (is_sub_group) {
        io.control.ext_mode := "b0011".U
    } .elsewhen (isInstr("SBB")) {
        io.control.ext_mode := "b0101".U
    } .elsewhen (isInstr("AND")) {
        io.control.ext_mode := "b1000".U
    } .elsewhen (isInstr("OR")) {
        io.control.ext_mode := "b1001".U
    } .elsewhen (isInstr("XOR")) {
        io.control.ext_mode := "b1010".U
    } .elsewhen (isInstr("NOT")) {
        io.control.ext_mode := "b1011".U
    } .elsewhen (isInstr("SHIFTL")) {
        io.control.ext_mode := "b1100".U
    } .elsewhen (isInstr("SHIFTA")) {
        io.control.ext_mode := "b1101".U
    } .elsewhen (isInstr("MVIH")) {
        io.control.ext_mode := "b1110".U
    } .elsewhen (isInstr("MVIL")) {
        io.control.ext_mode := "b1111".U
    } othervise { // NOP
        io.control.ext_mode := 0.U
    }

    // TODO: implement following
    io.control.carry_write := 0.U
    io.control.jmp_mode := 0.U
    io.control.stack_op := 0.U
    io.control.mem_data_src := 0.U
    io.control.mem_addr_src := 0.U
    io.control.mem_read := 0.U
    io.control.mem_write := 0.U
    io.control.rd_src := 0.U
}

object ControlUnitMain extends App {
  println("Generating ALU")
  (new chisel3.stage.ChiselStage).emitVerilog(new ControlUnit(), Array("--target-dir", "generated"))
}
