package lyng

import chisel3._
import chisel3.util._

/**
 *  This Module is a Random Access memory that fetches the data in one clock cycle 
 *  This is not realistic but for could be used for testing if the processor works
 */
class Memory(addr_bits : Int, data_bits : Int) extends Module {

    val io = IO(new Bundle {
        val mem_read = Input(UInt(1.W))
        val mem_write = Input(UInt(1.W))
        val data_in = Input(UInt(data_bits.W))
        val addr = Input(UInt(addr_bits.W))
        val ready = Output(UInt(1.W))
        val data_out = Output(UInt(data_bits.W))
    })

    val data = Mem(1 << (addr_bits - 1), UInt(data_bits.W))
    
    //The requested data is always ready in a clock cycle
    io.ready := 1.U  
    io.data_out := 0.U

    when(io.mem_write === 1.U) {
        data(io.addr) := io.data_in

    } .elsewhen(io.mem_read === 1.U) {
        io.data_out := data(io.addr)
    }
}   
    
