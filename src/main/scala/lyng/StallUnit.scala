package lyng

import chisel3._
import chisel3.util._

class StallUnit extends Module {
    val io = IO(new Bundle{
        val ex_jump = Input(UInt(1.W))
        val ex_me_jump = Input(UInt(1.W))
        val instr_mem_read = Input(UInt(1.W))
        val instr_mem_valid = Input(UInt(1.W))
        val data_mem_read = Input(UInt(1.W))
        val data_mem_valid = Input(UInt(1.W))
        val conflict_stall = Input(UInt(1.W))

        val pc_write = Output(UInt(1.W))
        val if_id_mode = Output(UInt(2.W))
        val id_ex_mode = Output(UInt(2.W))
        val ex_me_mode = Output(UInt(2.W))
        val me_wb_mode = Output(UInt(2.W))

        val error = Output(UInt(1.W))
    })


    val NORMAL = "b00".U
    val HOLD  = "b01".U
    val NOP   = "b10".U


    val mem_stall = (io.instr_mem_read & !io.instr_mem_valid) | (io.data_mem_read & !io.data_mem_valid)

    when(mem_stall === 1.U) {
        io.pc_write := 0.U
        io.if_id_mode := HOLD
        io.id_ex_mode := HOLD 
        io.ex_me_mode := HOLD 
        io.me_wb_mode := HOLD
        io.error := 0.U
    }.elsewhen(io.conflict_stall === 1.U && io.ex_me_jump === 0.U) {
        io.pc_write := 0.U
        io.if_id_mode := HOLD
        io.id_ex_mode := HOLD 
        io.ex_me_mode := NOP 
        io.me_wb_mode := NORMAL
        io.error := 0.U
    }.elsewhen(io.conflict_stall === 1.U && io.ex_me_jump === 1.U) {
        io.pc_write := 0.U
        io.if_id_mode := HOLD
        io.id_ex_mode := HOLD
        io.ex_me_mode := NOP 
        io.me_wb_mode := NORMAL
        io.error := 1.U
    }.elsewhen(io.ex_jump === 1.U && io.ex_me_jump === 0.U) {
        io.pc_write := 1.U
        io.if_id_mode := NOP
        io.id_ex_mode := NOP
        io.ex_me_mode := NORMAL 
        io.me_wb_mode := NORMAL
        io.error := 0.U
    }.elsewhen(io.ex_jump === 0.U && io.ex_me_jump === 1.U) {
        io.pc_write := 1.U
        io.if_id_mode := NOP
        io.id_ex_mode := NORMAL
        io.ex_me_mode := NORMAL 
        io.me_wb_mode := NORMAL
        io.error := 0.U
    }.elsewhen(io.ex_jump === 1.U && io.ex_me_jump === 1.U) {
        io.pc_write := 1.U
        io.if_id_mode := NOP
        io.id_ex_mode := NOP
        io.ex_me_mode := NORMAL 
        io.me_wb_mode := NORMAL
        io.error := 1.U
    }.otherwise {
        io.pc_write := 1.U
        io.if_id_mode := NORMAL
        io.id_ex_mode := NORMAL
        io.ex_me_mode := NORMAL 
        io.me_wb_mode := NORMAL
        io.error := 0.U
    }


}