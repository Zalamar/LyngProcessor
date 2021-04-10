/*
 * ALU unit
 *
 * Author: Tobias Rydberg (tobiasrydberg@pm.me)
 *
 */

package lyng.execute

import chisel3._

// TODO: Fix and put opcodes in shared file
// TODO: Share SUB and ADD circuit since they never run at the same time

class ALU extends Module {
  val io = IO(new Bundle {
    val alu_in1 = Input(SInt(16.W))
    val alu_in2 = Input(SInt(16.W))
    val alu_opcode = Input(Bits(5.W))
    val alu_out = Output(SInt(16.W))
    val carry_out = Output(UInt())
  })

  val carry = RegInit(0.U(1.W))
  io.carry_out := carry

  val s_alu_in1 = io.alu_in1.asSInt()
  val s_alu_in2 = io.alu_in2.asSInt()
  val s_alu_out = Wire(SInt(17.W))
  io.alu_out := s_alu_out
  s_alu_out := 0.S

  when(io.alu_opcode === 0.U) { // ADD/ADDI
    s_alu_out := s_alu_in1 + s_alu_in2
    carry := !s_alu_out(16) & s_alu_in1(15) & s_alu_in2(15)
  } .elsewhen(io.alu_opcode === 1.U) { // ADC
    when(carry===1.U) { // TODO: Share with SUB
      s_alu_out := s_alu_in1 + s_alu_in2 + 1.S
    } .otherwise {
      s_alu_out := s_alu_in1 + s_alu_in2
    }
    carry := !s_alu_out(16) & s_alu_in1(15) & s_alu_in2(15)
  } .elsewhen(io.alu_opcode === 2.U) { // SUB/SUBI TODO: Share with ADD
    s_alu_out := s_alu_in1 - s_alu_in2
    carry := !s_alu_out(16) & s_alu_in1(15) & s_alu_in2(15)
  } .elsewhen(io.alu_opcode === 3.U) { // SBB
    when(carry===1.U) { // TODO: Share with SUB
      s_alu_out := s_alu_in1 + s_alu_in2 - 1.S
    } .otherwise {
      s_alu_out := s_alu_in1 + s_alu_in2
    }
    carry := !s_alu_out(16) & s_alu_in1(15) & s_alu_in2(15)
  } .elsewhen(io.alu_opcode === 3.U) { // AND
    s_alu_out := s_alu_in1 & s_alu_in2
  } .elsewhen(io.alu_opcode === 4.U) { // OR
    s_alu_out := s_alu_in1 | s_alu_in2
  } .elsewhen(io.alu_opcode === 5.U) { // XOR
    s_alu_out := s_alu_in1 ^ s_alu_in2
  } .elsewhen(io.alu_opcode === 6.U) { // NOT
    s_alu_out := ~s_alu_in1
  } .elsewhen(io.alu_opcode === 7.U) { // SHIFTL
    s_alu_out := s_alu_in1 << 1 // TODO: Variable shift amount
  } .elsewhen(io.alu_opcode === 8.U) { // SHIFTR
    s_alu_out := s_alu_in1 >> 1
  } .elsewhen(io.alu_opcode === 9.U) { // Pass Rs1
    s_alu_out := s_alu_in1 // This can be turn into an ADD with a register tied to 0
  } .otherwise { // Carry
    carry := 1.U
  }

}

object ALUMain extends App {
  println("Generating ALU")
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU(), Array("--target-dir", "generated"))
}
