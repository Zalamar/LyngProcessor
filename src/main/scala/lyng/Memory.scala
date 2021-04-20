package lyng

import chisel3._
import chisel3.util._

/**
 *  This Module is a Random Access memory that fetches the data in one clock cycle 
 *  This is not realistic but for could be used for testing if the processor works
 *
 *    data_port_word is the
 */
class Memory(addr_size : Int, word_size : Int, double_word_port: Boolean, fixed_output : Boolean = false, fixed_out_addr : Int = 0) extends Module {

    val io = IO(new Bundle {
        val mem_read = Input(UInt(1.W))
        val mem_write = Input(UInt(1.W))
        val data_in = Input(UInt((word_size * (if (double_word_port) 2 else 1)).W))
        val addr = Input(UInt(addr_size.W))
        val valid = Output(UInt(1.W))
        val data_out = Output(UInt((word_size *(if (double_word_port) 2 else 1)).W))
    })

    val data = Mem(1 << addr_size, UInt(word_size.W))
    //The requested data is always ready in this memory module
    io.valid := 1.U 
    io.data_out := 0.U

    when(io.mem_write === 1.U) {
        if(double_word_port) {
            data(io.addr) := io.data_in(word_size - 1, 0)
            data(io.addr + 1.U) := io.data_in((word_size << 1) - 1, word_size) 
        } else {
            data(io.addr) := io.data_in
        }
    }.elsewhen(io.mem_read === 1.U) {
        //TODO Experiment 
        if(double_word_port) {
            io.data_out := Cat(data(io.addr + 1.U), data(io.addr))
        } else {
            io.data_out := data(io.addr)
        }
    }
}   


