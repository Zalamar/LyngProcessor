package lyng.decode

import chisel3._
import chisel3.util._

class DecodeTop extends Module {
    val io = IO(new Bundle {
        // inputs from IF/ID reg
        val in_instr = Input(UInt(16.W))
        // inputs from WB
        val in_wr_addr = Input(UInt(3.W))
        val in_data_out = Input(UInt(16.W))
        val in_alu_res = Input(UInt(16.W))
        val in_wb_reg_write = Input(UInt(1.W))
        // outputs to ID/EX reg
        val out_rd_addr = Output(UInt(3.W))
        val out_control = Output(new ControlUnitSig)
        val out_rs1 = Output(UInt(16.W))
        val out_rs2 = Output(UInt(16.W))
        val out_rd = Output(UInt(16.W))
        val out_imm = Output(UInt(11.W))
    })

    // spliting in_instruction
    val rs1_addr = io.in_instr(8,10)
    val rs2_addr = io.in_instr(2,4)
    val rd_addr = io.in_instr(5,7)
    val imm = io.in_instr(0,10)
    val opcode = io.in_instr(11,15)
    val func = io.in_instr(0,1)

    // control unit
    val control_unit = Module(new ControlUnit)
    control_unit.io.opcode := opcode
    control_unit.io.func := func
    io.out_control := control_unit.io.control

    // register file
    val reg_file = Module(new RegFile)
    reg_file.io.rs1_addr := rs1_addr
    reg_file.io.rs2_addr := rs2_addr
    reg_file.io.rd_addr := rd_addr
    reg_file.io.wr_addr := io.in_wr_addr
    reg_file.io.wr_in := Mux(control_unit.io.control.rd_src,io.in_data_out, io.in_alu_res)
    reg_file.io.reg_write := io.in_wb_reg_write
    io.out_rs1 := reg_file.io.rs1_out
    io.out_rs2 := reg_file.io.rs2_out
    io.out_rd := reg_file.io.rd_out

    // control unit
    val control_unit = Module(new ControlUnit)
    control_unit.io.opcode := opcode
    control_unit.io.func := func
    io.out_control := control_unit.io.control

    // extra outputs
    io.out_rd_addr := rd_addr
    io.out_imm := imm
}
