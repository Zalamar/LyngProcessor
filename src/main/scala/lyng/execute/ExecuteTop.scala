package lyng.execute

import chisel3._
import chisel3.util._
import lyng.ControlUnitSig

class ExecuteIn extends Bundle {
    // Registers
    val rs1 = Input(UInt(16.W))
    val rs2 = Input(UInt(16.W))
    val imm = Input(UInt(11.W))
    val prop_rs1 = Input(UInt(1.W))
    val prop_rs2 = Input(UInt(1.W))
    val ex_forward = Input(UInt(16.W))
    val me_forward = Input(UInt(16.W))
}

class ExecuteOut extends Bundle {
    // Outputs
    val jump = Output(UInt(1.W))
    val jump_amt = Output(UInt(17.W))
    val alu_res = Output(UInt(16.W))
}


class ExecuteTop extends Module{
  val io = IO(new Bundle {

    val ctrl = Input(new ControlUnitSig)

    val in = new ExecuteIn
    val out = new ExecuteOut
  })

  val ALU = Module(new ALU())
  val extender = Module(new Extender())

  ALU.io.alu_opcode := io.ctrl.alu_op
  io.out.alu_res := ALU.io.alu_out.asUInt()

  extender.io.immediate_in := io.in.imm
  extender.io.ext_mode := io.ctrl.ext_mode

  ALU.io.alu_in1 := io.in.rs1.asSInt()
  when(io.in.prop_rs1 === "b01".U) {
    ALU.io.alu_in1 := io.in.me_forward.asSInt()
  } .elsewhen(io.in.prop_rs1 === "b10".U) {
    ALU.io.alu_in1 := io.in.ex_forward.asSInt()
  }


  val prop_rs2_output = Wire(Bits())
  prop_rs2_output := io.in.rs2
  when(io.in.prop_rs2 === "b01".U) {
    prop_rs2_output := io.in.me_forward
  }.elsewhen(io.in.prop_rs2 === "b10".U) {
    prop_rs2_output := io.in.ex_forward
  }

  when (io.ctrl.alu_src === 0.U) {
    ALU.io.alu_in2 := prop_rs2_output.asSInt()
  } .otherwise {
    ALU.io.alu_in2 := extender.io.immediate_out.asSInt()
  }

  io.out.alu_res := ALU.io.alu_out.asUInt()

  io.out.jump := 0.U
  switch(io.ctrl.jmp_mode) {
    is(1.U) {
      io.out.jump := 1.U
    }
    is(2.U) {
      io.out.jump := ALU.io.gez
    }
    is(3.U) {
      io.out.jump := ALU.io.zero
    }
    is(4.U) {
      io.out.jump := !ALU.io.gez | ALU.io.zero
    }
    is(5.U) {
      io.out.jump := ALU.io.carry_out
    }
  }

  when (io.out.jump === 0.U) {
    io.out.jump_amt := 0.U
  } .otherwise {
    io.out.jump_amt := extender.io.immediate_out << 1
  }
}
