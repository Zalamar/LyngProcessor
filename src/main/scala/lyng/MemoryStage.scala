package lyng

import chisel3._
import chisel3.util._


class MemoryStage extends Module {
    val io = IO(new Bundle {
        //Input from EX/ME
        val in_jump = Input(UInt(1.W))
        val in_jump_amt = Input(UInt(17.W))
        val in_alu_res = Input(UInt(16.W))
        val in_PC = Input(UInt(17.W))
        val in_RD = Input(UInt(16.W))
        val in_rw_addr = Input(UInt(3.W))
        //Control Signals (from EX/ME)
        val stack_op = Input(UInt(2.W))
        val mem_data_src = Input(UInt(1.W))
        val mem_addr_src = Input(UInt(1.W))
        val mem_read = Input(UInt(1.W))
        val mem_write = Input(UInt(1.W))
        //Controls from propagation
        val prop_ME_ME = Input(UInt(1.W))
        //Propagation values
        val in_rw_value = Input(UInt(16.W))

        //Outputs to ME/WB
        val out_data_out = Output(UInt(16.W))
        val out_alu_res = Output(UInt(16.W))
        val out_rw_addr = Output(UInt(3.W))

        //Outputs to PC
        val out_return_addr = Output(UInt(17.W))
        val out_call = Output(UInt(17.W))
        val out_jump = Output(UInt(17.W))

        //Outputs to stall unit
        val data_ready_out = Output(UInt(1.W))
    })

    val memory = Module(new Memory(16, 16))

    // Stack Pointer
    val stack_pointer = Reg(UInt(16.W)) //TODO Init to 0xFFFF

    //Internal Signals
    val sp_out = Wire(UInt(16.W))
    val mem_data_in_no_prop = Wire(UInt(16.W))
    

    //Outputs to PC calc unit    
    io.out_call := (io.in_PC.asSInt + io.in_jump_amt.asSInt).asUInt
    io.out_jump := io.in_RD << 1
    io.out_return_addr := memory.io.data_out << 1

    //Other Signals
    io.out_alu_res := io.in_alu_res
    io.out_rw_addr := io.in_rw_addr

    sp_out := stack_pointer

    //Stack unit
    switch(io.stack_op) {
        is("b00".U) { //No-Stack OP
            sp_out := stack_pointer
            stack_pointer := sp_out
        }
        is("b01".U) { //MOVSP
            stack_pointer := io.in_RD
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
    memory.io.mem_write := io.mem_write
    memory.io.mem_read := io.mem_read
    memory.io.addr := Mux(io.mem_addr_src === true.B, sp_out, io.in_alu_res.asUInt)
    mem_data_in_no_prop := Mux(io.mem_data_src === true.B, io.in_PC >> 1, io.in_RD)
    memory.io.data_in := Mux(io.prop_ME_ME === true.B, io.in_rw_value.asUInt, mem_data_in_no_prop)
    io.out_data_out := memory.io.data_out
    io.data_ready_out := memory.io.ready
}

object MemStageMain extends App {
  println("Generating MEM Stage")
  (new chisel3.stage.ChiselStage).emitVerilog(new MemoryStage(), Array("--target-dir", "generated"))
}
