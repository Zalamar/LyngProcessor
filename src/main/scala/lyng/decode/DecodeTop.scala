package lyng.decode

import chisel3._
import chisel3.util._
import lyng.ControlUnitSig

class DecodeIn extends Bundle {
        // inputs from IF/ID reg
        val instr = Input(UInt(16.W))
        // inputs from WB
        val rw_addr = Input(UInt(3.W))
        val wb_reg_write = Input(UInt(1.W))
        val wb_rw_value = Input(UInt(16.W))
}

class DecodeOut extends Bundle {
        // outputs to ID/EX reg
        val rd_addr = Output(UInt(3.W))
        val rs1 = Output(UInt(16.W))
        val rs2 = Output(UInt(16.W))
        val rs1_addr = Output(UInt(3.W))
        val rs2_addr = Output(UInt(3.W))
        val rd = Output(UInt(16.W))
        val imm = Output(UInt(11.W))
        val ctrl = Output(new ControlUnitSig)
}

class DecodeTop extends Module {
    val io = IO(new Bundle {
        val in = new DecodeIn
        val out = new DecodeOut
    })

    // spliting in_instruction
    val rs1_addr = io.in.instr(10,8)
    val rs2_addr = io.in.instr(4,2)
    val rd_addr = io.in.instr(7,5)
    val imm = io.in.instr(10,0)
    val opcode = io.in.instr(15,11)
    val func = io.in.instr(1,0)

    // control unit
    val control_unit = Module(new ControlUnit)
    control_unit.io.opcode := opcode
    control_unit.io.func := func
    io.out.ctrl := control_unit.io.ctrl

    // register file
    val reg_file = Module(new RegFile)
    reg_file.io.rs1_addr := rs1_addr
    reg_file.io.rs2_addr := Mux(control_unit.io.ctrl.rd_addr_src === 1.U, rd_addr, rs2_addr)
    reg_file.io.rd_addr := rd_addr
    reg_file.io.wr_addr := io.in.rw_addr
    reg_file.io.wr_in := io.in.wb_rw_value
    reg_file.io.reg_write := io.in.wb_reg_write
    io.out.rs1 := reg_file.io.rs1_out
    io.out.rs2 := reg_file.io.rs2_out
    io.out.rd := reg_file.io.rd_out

    // extra outputs
    io.out.rd_addr := rd_addr
    io.out.imm := imm
    io.out.rs1_addr := rs1_addr
    io.out.rs2_addr := rs2_addr
}
