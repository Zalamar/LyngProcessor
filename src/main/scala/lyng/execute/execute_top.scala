package lyng.execute

import chisel3._
import chisel3.util._

class execute_top extends Module{
  val io = IO(new Bundle {
    // Registers
    val r1 = Input(SInt(16.W))
    val r3 = Input(SInt(16.W))
    val immediate = Input(SInt(11.W))
    val alu_res_forward = Input(SInt(16.W))
    val writeback_forward = Input(SInt(16.W))
    // Control signals
    val prop_r1 = Input(Bits(2.W))
    val prop_r3 = Input(Bits(2.W))
    val alu_src = Input(Bits(1.W))
    val ext_mode = Input(Bits(3.W))
    val alu_op = Input(Bits(4.W))
    val jmp_mode = Input(Bits(3.W))
    // Outputs
    val jump = Output(Bits(1.W))
    val jump_amt = Output(SInt(16.W))
    val alu_res = Output(SInt(16.W))
  })

  val ALU = Module(new ALU())
  val extender = Module(new extender())

  ALU.io.alu_opcode := io.alu_op

  extender.io.immediate_in := io.immediate
  extender.io.ext_mode := io.ext_mode

  when(io.prop_r1 === 0.U) {
    ALU.io.alu_in1 := io.writeback_forward
  } .elsewhen(io.prop_r1 === 1.U) {
    ALU.io.alu_in1 := io.alu_res_forward
  } .otherwise {
    ALU.io.alu_in1 := io.r1
  }

  val prop_r3_output = Wire(Bits())

  when(io.prop_r3 === 0.U) {
    prop_r3_output := io.writeback_forward
  } .elsewhen(io.prop_r3 === 1.U) {
    prop_r3_output := io.alu_res_forward
  } .otherwise {
    prop_r3_output := io.r3
  }

  when (io.alu_src === 0.U) {
    ALU.io.alu_in2 := prop_r3_output
  } .otherwise {
    ALU.io.alu_in2 := extender.io.immediate_out
  }

  io.jump := 0.U
  switch(io.jmp_mode) {
    is(1.U) {
      io.jump := 1.U
    }
    is(2.U) {
      io.jump := ALU.io.gez
    }
    is(3.U) {
      io.jump := ALU.io.zero
    }
    is(4.U) {
      io.jump := !ALU.io.gez ^ ALU.io.zero
    }
    is(5.U) {
      io.jump := ALU.io.carry_out
    }
  }

  when (io.jump === 0.U) {
    io.jump_amt := 0.U
  } .otherwise {
    io.jump_amt := extender.io.immediate_out >> 1
  }

}
