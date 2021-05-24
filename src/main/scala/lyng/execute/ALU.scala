/*
 * ALU unit
 *
 * Author: Tobias Rydberg (tobiasrydberg@pm.me)
 *
 */

package lyng.execute

import chisel3._

class ALU extends Module {
  val io = IO(new Bundle {
    val alu_in1 = Input(SInt(16.W))
    val alu_in2 = Input(SInt(16.W))
    val alu_opcode = Input(Bits(4.W))
    val alu_out = Output(SInt(16.W))
    val carry_out = Output(UInt())
    val gez = Output(Bits(1.W))
    val zero = Output(Bits(1.W))
  })

  val carry = RegInit(0.U(1.W))
  io.carry_out := carry

  val s_alu_in1 = io.alu_in1.asSInt()
  val s_alu_in2 = io.alu_in2.asSInt()
  val s_alu_out = Wire(SInt(17.W))
  io.alu_out := s_alu_out
  s_alu_out := 0.S
  io.gez := io.alu_in1 >= io.alu_in2
  io.zero := io.alu_in1 === io.alu_in2

  when(io.alu_opcode === 1.U) { // ADD/ADDI
    carry := 1.U
  } .elsewhen(io.alu_opcode === 2.U) { // ADD/ADDI
    s_alu_out := s_alu_in1 + s_alu_in2
    when((!s_alu_in1(15) & !s_alu_in2(15) & s_alu_out(15)) | (s_alu_in1(15) & s_alu_in2(15) & !s_alu_out(15))) {
      carry := 1.U
    } .otherwise {
      carry := 0.U
    }
  } .elsewhen(io.alu_opcode === 3.U) { // ADC
    when(carry===1.U) {
      s_alu_out := s_alu_in1 + s_alu_in2 + 1.S
    } .otherwise {
      s_alu_out := s_alu_in1 + s_alu_in2
    }
    when((!s_alu_in1(15) & !s_alu_in2(15) & s_alu_out(15)) | (s_alu_in1(15) & s_alu_in2(15) & !s_alu_out(15))) {
      carry := 1.U
    } .otherwise {
      carry := 0.U
    }
  } .elsewhen(io.alu_opcode === 4.U) { // SUB/SUBI
    s_alu_out := s_alu_in1 - s_alu_in2
    when(s_alu_in1 < s_alu_in2) {
      carry := 1.U
    } .otherwise {
      carry := 0.U
    }
  } .elsewhen(io.alu_opcode === 5.U) { // SBB
    when(carry===1.U) {
      s_alu_out := s_alu_in1 - s_alu_in2 - 1.S
    } .otherwise {
      s_alu_out := s_alu_in1 - s_alu_in2
    }
    when(s_alu_in1 < s_alu_in2) {
      carry := 1.U
    } .otherwise {
      carry := 0.U
    }
  } .elsewhen(io.alu_opcode === 8.U) { // AND
    s_alu_out := s_alu_in1 & s_alu_in2
  } .elsewhen(io.alu_opcode === 9.U) { // OR
    s_alu_out := s_alu_in1 | s_alu_in2
  } .elsewhen(io.alu_opcode === 10.U) { // XOR
    s_alu_out := s_alu_in1 ^ s_alu_in2
  } .elsewhen(io.alu_opcode === 11.U) { // NOT
    s_alu_out := ~s_alu_in1
  } .elsewhen(io.alu_opcode === 12.U) { // SHIFTL
    s_alu_out := s_alu_in1 << s_alu_in2.asUInt()
  } .elsewhen(io.alu_opcode === 13.U) { // SHIFTR
    s_alu_out := (s_alu_in1.asUInt() >> s_alu_in2.asUInt()).asSInt()
  } .elsewhen(io.alu_opcode === 14.U) { // MVIH
    s_alu_out := (s_alu_in2.asUInt() << 8).asSInt() | (s_alu_in1 & 0x0FF.S)
  } .elsewhen(io.alu_opcode === 15.U) { // MVIL
    s_alu_out := (s_alu_in1 & 0xFF00.S) | s_alu_in2
  }

}

/**
object ALUMain extends App {
  println("Generating ALU")
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU(), Array("--target-dir", "generated"))
}
*/