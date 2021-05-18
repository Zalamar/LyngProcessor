package lyng.memory

import lyng.Memory
import lyng.ControlUnitSig
import chisel3._
import chisel3.util._

class MemoryStageIn extends Bundle {
        //Input from EX/ME
        val jump = Input(UInt(1.W))
        val jump_amt = Input(UInt(16.W))
        val alu_res = Input(UInt(16.W))
        val pc = Input(UInt(16.W))
        val rd_in = Input(UInt(16.W))
        val rw_addr = Input(UInt(3.W))
        //propagation
        val prop_ME_ME = Input(UInt(1.W))
        val rw_value = Input(UInt(16.W))

        //Input from memory
        val data_out = Input(UInt(16.W))
}


class MemoryStageOut extends Bundle {

    //Outputs to Memory
    val data_in = Output(UInt(16.W))
    val addr = Output(UInt(16.W))

    //Outputs to ME/WB
    val data_out = Output(UInt(16.W))
    val alu_res = Output(UInt(16.W))
    val rw_addr = Output(UInt(3.W))
    val rd_out = Output(UInt(16.W))

    //Outputs to PC
    val return_addr = Output(UInt(16.W))
    val call = Output(UInt(16.W))
    val jump = Output(UInt(16.W))
}

class MemoryTop extends Module {
    val io = IO(new Bundle {

        val ctrl = Input(new ControlUnitSig)

        val in = new MemoryStageIn
        val out = new MemoryStageOut
    })

    // Stack Pointer
    val stack_pointer = Reg(UInt(16.W)) //TODO Init to 0xFFFF

    //Internal Signals
    val sp_out = Wire(UInt(16.W))

    val actual_rd = Mux(io.in.prop_ME_ME === 1.U, io.in.rw_value, io.in.rd_in)

    //Outputs to PC calc unit    
    io.out.call := actual_rd
    io.out.jump := (io.in.pc.asSInt + io.in.jump_amt.asSInt).asUInt
    io.out.return_addr := io.in.data_out

    //Other Signals
    io.out.alu_res := io.in.alu_res
    io.out.rw_addr := io.in.rw_addr
    io.out.data_out := io.in.data_out
    io.out.rd_out := io.in.data_out

    sp_out := stack_pointer

    //Stack unit
    switch(io.ctrl.stack_op) {
        is("b00".U) { //No-Stack OP
            sp_out := stack_pointer
            stack_pointer := sp_out
        }
        is("b01".U) { //MOVSP
            stack_pointer := actual_rd
            sp_out := stack_pointer
        }
        is("b10".U) { //PUSH
            sp_out := stack_pointer
            stack_pointer := sp_out - 2.U
        }
        is("b11".U) { //POP
            sp_out := stack_pointer + 2.U
            stack_pointer := sp_out
        }

    }

    //Memory 
    io.out.addr := Mux(io.ctrl.mem_addr_src === 1.U, sp_out, io.in.alu_res.asUInt)
    io.out.data_in := Mux(io.ctrl.mem_data_src === 1.U, io.in.pc, actual_rd)
}

/**
object MemStageMain extends App {
  println("Generating MEM Stage")
  (new chisel3.stage.ChiselStage).emitVerilog(new MemoryTop(), Array("--target-dir", "generated"))
}*/
