package lyng.decode

import chisel3._
import chisel3.util._

class RegFile extends Module {
    val io = IO(new Bundle {
        // control signal
        val reg_write = Input(UInt(1.W))
        // register inputs
        val rs1_addr = Input(UInt(3.W))
        val rs2_addr = Input(UInt(3.W))
        val rd_addr = Input(UInt(3.W))
        val wr_addr = Input(UInt(3.W))
        val wr_in = Input(UInt(16.W))
        // register outputs
        val rs1_out = Output(UInt(16.W))
        val rs2_out = Output(UInt(16.W))
        val rd_out = Output(UInt(16.W))
    })

    val register = Reg(Vec(8, UInt(16.W)))

    // set output
    io.rs1_out := register(io.rs1_addr)
    io.rs2_out := register(io.rs2_addr)
    io.rd_out := register(io.rd_addr)

    when (io.reg_write === true.B) {
        // write input
        register(io.wr_addr) := io.wr_in
        // forwading (gets input to output in the same cycle)
        when (io.wr_addr === io.rs1_addr) {
            io.rs1_out := io.wr_in
        }
        when (io.wr_addr === io.rs2_addr) {
            io.rs2_out := io.wr_in
        }
        when (io.wr_addr === io.rd_addr) {
            io.rd_out := io.wr_in
        }
    }

}
