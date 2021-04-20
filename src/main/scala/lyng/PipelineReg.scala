package lyng

import chisel3._
import chisel3.util._


class PipelineReg[T <: Data](dataFactory : () => T) extends Module {
    val io = IO(new Bundle{
        val in = Input(dataFactory())
        val ctrl = Input(UInt(2.W))

        val out = Output(dataFactory())
    })

    //val reg_write = Wire(UInt(1.W))
    val reg = withReset(io.ctrl === "b10".U)(RegEnable(io.in, 0.U.asTypeOf(dataFactory()), io.ctrl === "b00".U))
    
    io.out := reg
    /*
    when(io.ctrl === "b01".U) { //Hold
        reg_write := 0.U
    }.elsewhen(io.ctrl === "b10".U) { //Nop
        reg_write := 0.U
    }.otherwise { //Normal
        reg_write := 1.U
    }*/
}

