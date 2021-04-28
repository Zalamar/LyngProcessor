package lyng

import chisel3._
import chisel3.util._

/**
 *  This Module is a Random Access memory that fetches the data in one clock cycle 
 *  This is not realistic but for could be used for testing if the processor works
 *
 *    data_port_word is the
 */
class Memory(addr_size : Int, word_size : Int) extends Module {

    val io = IO(new Bundle {
        val mem_read = Input(UInt(1.W))
        val mem_write = Input(UInt(1.W))
        val data_in = Input(UInt(word_size.W))
        val addr = Input(UInt(addr_size.W))
        val valid = Output(UInt(1.W))
        val data_out = Output(UInt(word_size.W))
    })

    //val data = Mem(1 << addr_size, UInt(word_size.W))
    //The requested data is always ready in this memory module


    val data = SyncReadMem(1 << addr_size, UInt(word_size.W))
    io.data_out := DontCare
    when(io.mem_write === 1.U || io.mem_read === 1.U) {
        val rdwrPort = data(io.addr)
        when (io.mem_write === 1.U) { 
            rdwrPort := io.data_in 
        }
        .otherwise { 
            io.data_out := rdwrPort 
        }
    }
    io.valid := 1.U 
}   


