package lyng.execute

import chisel3._
import chisel3.util._
import lyng.ControlUnitSig

class ExecuteIn extends Bundle {
    // Registers
    val rs1 = Input(UInt(16.W))
    val rs2 = Input(UInt(16.W))
    val rd  = Input(UInt(16.W))
    val imm = Input(UInt(11.W))
    val prop_rs1 = Input(UInt(2.W))
    val prop_rs2 = Input(UInt(2.W))
    val prop_rd = Input(UInt(2.W))
    val ex_forward = Input(UInt(16.W))
    val me_forward = Input(UInt(16.W))
}

class ExecuteOut extends Bundle {
    // Outputs
    val alu_res = Output(UInt(16.W))
    val rd = Output(UInt(16.W))
    val gez = Output(UInt(1.W))
    val zero = Output(UInt(1.W))
    val carry_out = Output(UInt(1.W))
}


class ExecuteTop extends Module{
  val io = IO(new Bundle {

    val ctrl = Input(new ControlUnitSig)

    val in = new ExecuteIn
    val out = new ExecuteOut
  })

  val ALU = Module(new ALU())
  val extender = Module(new Extender())

  val alu_src_output = Wire(Bits())

  ALU.io.alu_opcode := io.ctrl.alu_op
  io.out.alu_res := ALU.io.alu_out.asUInt()

  extender.io.immediate_in := io.in.imm
  extender.io.ext_mode := io.ctrl.ext_mode

  io.out.zero := ALU.io.zero
  io.out.gez := ALU.io.gez
  io.out.carry_out := ALU.io.carry_out

  ALU.io.alu_in1 := io.in.rs1.asSInt()
  when(io.in.prop_rs1 === 1.U) {
    ALU.io.alu_in1 := io.in.me_forward.asSInt()
  } .elsewhen(io.in.prop_rs1 === 2.U) {
    ALU.io.alu_in1 := io.in.ex_forward.asSInt()
  }


  ALU.io.alu_in2 := alu_src_output.asSInt()
  when(io.in.prop_rs2 === 1.U) {
    ALU.io.alu_in2 := io.in.me_forward.asSInt()
  }.elsewhen(io.in.prop_rs2 === 2.U) {
    ALU.io.alu_in2 := io.in.ex_forward.asSInt()
  }

  io.out.rd := io.in.rd
  when(io.in.prop_rd === 1.U) {
    io.out.rd := io.in.me_forward
  }.elsewhen(io.in.prop_rd === 2.U) {
    io.out.rd := io.in.ex_forward
  }

  when (io.ctrl.alu_src === 0.U) {
    alu_src_output := io.in.rs2
  } .otherwise {
    alu_src_output := extender.io.immediate_out
  }

  io.out.alu_res := ALU.io.alu_out.asUInt()
}
