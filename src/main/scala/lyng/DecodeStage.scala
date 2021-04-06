package lyng

import chisel3._
import chisel3.util._

class DecodeStage extends Module {
    val io = IO(new Bundle {
        val instr = Input(UInt(16.W))
        val rs1_addr = Output(UInt(3.W))
        val rs2_addr = Output(UInt(3.W))
        val rd_addr = Output(UInt(3.W))
        val imm = Output(UInt(11.W))
        val opcode = Output(UInt(5.W))
        val func = Output(UInt(2.W))
    })

    io.rs1_addr := io.instr(8,10)
    io.rs2_addr := io.instr(2,4)
    io.rd_addr := io.instr(5,7)
    io.imm := io.instr(0,10)
    io.opcode := io.instr(11,15)
    io.func := io.instr(0,1)
}
