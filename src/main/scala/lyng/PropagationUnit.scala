package lyng

import chisel3._
import chisel3.util._



class StageCtrlIn extends Bundle {
    val reg_write = Input(UInt(1.W))
    val rw_addr = Input(UInt(3.W))
}


class EXPropagationUnit extends Module {
    val io = IO(new Bundle{
        val ex_me = new StageCtrlIn()
        val me_wb = new StageCtrlIn()
        val ex_me_mem_read = Input(UInt(1.W))
        val rx_addr = Input(UInt(3.W))
        val prop_rx = Output(UInt(2.W))
        val conflict_stall = Output(UInt(1.W))
    })

    val ex_me_conflict = io.ex_me.reg_write & (io.ex_me.rw_addr === io.rx_addr).asUInt()
    val me_wb_conflict = io.me_wb.reg_write & (io.me_wb.rw_addr === io.rx_addr).asUInt()
    val NO_PROPAGATION = "b00".U
    val EX_EX_PROPAGATION = "b10".U
    val ME_EX_PROPAGATION = "b01".U

    io.conflict_stall := io.ex_me_mem_read & io.ex_me.reg_write & (io.ex_me.rw_addr === io.rx_addr).asUInt()

    io.prop_rx := NO_PROPAGATION

    when(ex_me_conflict === 1.U) {
        io.prop_rx := EX_EX_PROPAGATION
    }.elsewhen(me_wb_conflict === 1.U) {
        io.prop_rx := ME_EX_PROPAGATION
    }
}

class MEPropagationUnit extends Module {
    val io = IO(new Bundle{
        val me_wb_mem_read = Input(UInt(1.W))
        val ex_me_mem_write = Input(UInt(1.W))
        val ex_me_rw_addr = Input(UInt(1.W))
        val me_wb_rw_addr = Input(UInt(1.W))
        val prop_ME_ME = Output(UInt(1.W))
    })

    io.prop_ME_ME := io.me_wb_mem_read & io.ex_me_mem_write & (io.ex_me_rw_addr === io.me_wb_rw_addr).asUInt()
}