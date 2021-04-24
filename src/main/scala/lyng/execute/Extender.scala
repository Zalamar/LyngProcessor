package lyng.execute

import chisel3._
import chisel3.util._

class Extender extends Module {
  val io = IO(new Bundle {
    val immediate_in = Input(Bits(11.W))
    val ext_mode = Input(Bits(3.W))
    val immediate_out = Output(Bits(16.W))
  })

  // See ALU_Control_Codes for opcodes
  // TODO: Could this be improved by doing it in steps?
  io.immediate_out := Cat("b00000000000".U, io.immediate_in(4,0))
  when(io.ext_mode === 0.U) {
    io.immediate_out := Cat("b00000000000".U, io.immediate_in(4,0))
  } .elsewhen(io.ext_mode === 1.U) {
    when (io.immediate_in(4) === 1.U) {
      io.immediate_out := Cat("b11111111111".U, io.immediate_in(4,0))
    } .otherwise {
      io.immediate_out := Cat("b00000000000".U, io.immediate_in(4,0))
    }
  } .elsewhen(io.ext_mode === 2.U) {
    io.immediate_out := Cat("b00000000".U, io.immediate_in(10,8), io.immediate_in(4,0))
  } .elsewhen(io.ext_mode === 3.U) {
    when (io.immediate_in(10) === 1.U) {
      io.immediate_out := Cat("b11111111".U, io.immediate_in(10,8), io.immediate_in(4,0))
    } .otherwise {
      io.immediate_out := Cat("b00000000".U, io.immediate_in(10,8), io.immediate_in(4,0))
    }
  } .elsewhen(io.ext_mode === 4.U) {
    io.immediate_out := Cat("b00000".U, io.immediate_in(10,0))
  } .otherwise {
    when (io.immediate_in(10) === 1.U) {
      io.immediate_out := Cat("b11111".U, io.immediate_in(10,0))
    } .otherwise {
      io.immediate_out := Cat("b00000".U, io.immediate_in(10,0))
    }
  }

}
