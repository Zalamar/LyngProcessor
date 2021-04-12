package lyng.decode

import chisel3._
import chisel3.util._

class DecodeStage extends Module {
    val io = IO(new Bundle {
        // inputs from IF/ID reg
        val instr = Input(UInt(16.W))
        // inputs from WB
        val wr_addr = Input(UInt(3.W))
        val wr_in = Input(UInt(16.W))
        val wb_reg_write = Input(UInt(1.W))
        // outputs to ID/EX reg
        val rd_addr = Output(UInt(3.W))
        val control = Output(new ControlSig)
        val rs1 = Output(UInt(16.W))
        val rs2 = Output(UInt(16.W))
        val rd = Output(UInt(16.W))
        val imm = Output(UInt(11.W))
    })

    // spliting instruction
    val rs1_addr = io.instr(8,10)
    val rs2_addr = io.instr(2,4)
    val rd_addr = io.instr(5,7)
    val imm = io.instr(0,10)
    val opcode = io.instr(11,15)
    val func = io.instr(0,1)

    // register file
    val reg_file = Module(new RegFile)
    reg_file.io.rs1_addr := rs1_addr
    reg_file.io.rs2_addr := rs2_addr
    reg_file.io.rd_addr := rd_addr
    reg_file.io.wr_addr := io.wr_addr
    reg_file.io.wr_in := io.wr_in
    reg_file.io.reg_write := io.wb_reg_write
    io.rs1 := reg_file.io.rs1_out
    io.rs2 := reg_file.io.rs2_out
    io.rd := reg_file.io.rd_out

    // control unit
    val control_unit = Module(new ControlUnit)
    control_unit.io.opcode := opcode
    control_unit.io.func := func
    io.control := control_unit.io.control

    // extra outputs
    io.rd_addr := rd_addr
    io.imm := imm
}
