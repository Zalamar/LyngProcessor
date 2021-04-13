package lyng.memory

import lyng.Memory
import lyng.ControlUnitOut
import chisel3._
import chisel3.util._

class MemoryStageIn extends Bundle {
        //Input from EX/ME
        val jump = Input(UInt(1.W))
        val jump_amt = Input(UInt(17.W))
        val alu_res = Input(UInt(16.W))
        val pc = Input(UInt(17.W))
        val rd = Input(UInt(16.W))
        val rw_addr = Input(UInt(3.W))
        //Controls from propagation
        val prop_ME_ME = Input(UInt(1.W))
        //Propagation values
        val rw_value = Input(UInt(16.W))
}


class MemoryStageOut extends Bundle {
    //Outputs to ME/WB
    val data_out = Output(UInt(16.W))
    val alu_res = Output(UInt(16.W))
    val rw_addr = Output(UInt(3.W))

    //Outputs to PC
    val return_addr = Output(UInt(17.W))
    val call = Output(UInt(17.W))
    val jump = Output(UInt(17.W))

    //Outputs to stall unit
    val data_valid = Output(UInt(1.W))    
}

class MemoryTop extends Module {
    val io = IO(new Bundle {

        val ctrl = Flipped(new ControlUnitOut)

        val in = new MemoryStageIn
        val out = new MemoryStageOut
    })

    val memory = Module(new Memory(16, 8, true))

    // Stack Pointer
    val stack_pointer = Reg(UInt(16.W)) //TODO Init to 0xFFFF

    //Internal Signals
    val sp_out = Wire(UInt(16.W))
    val mem_data_in_no_prop = Wire(UInt(16.W))
    

    //Outputs to PC calc unit    
    io.out.call := io.in.rd << 1
    io.out.jump := (io.in.pc.asSInt + io.in.jump_amt.asSInt).asUInt
    io.out.return_addr := memory.io.data_out << 1

    //Other Signals
    io.out.alu_res := io.in.alu_res
    io.out.rw_addr := io.in.rw_addr

    sp_out := stack_pointer

    //Stack unit
    switch(io.ctrl.stack_op) {
        is("b00".U) { //No-Stack OP
            sp_out := stack_pointer
            stack_pointer := sp_out
        }
        is("b01".U) { //MOVSP
            stack_pointer := io.in.rd
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
    memory.io.mem_write := io.ctrl.mem_write
    memory.io.mem_read := io.ctrl.mem_read
    memory.io.addr := Mux(io.ctrl.mem_addr_src === 1.U, sp_out, io.in.alu_res.asUInt)
    mem_data_in_no_prop := Mux(io.ctrl.mem_data_src === 1.U, io.in.pc >> 1, io.in.rd)
    memory.io.data_in := Mux(io.in.prop_ME_ME === 1.U, io.in.rw_value.asUInt, mem_data_in_no_prop)
    io.out.data_out := memory.io.data_out
    io.out.data_valid := memory.io.valid
}

