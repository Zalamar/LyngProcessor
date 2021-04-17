package lyng

import chisel3._
import chisel3.util._

class ControlUnitSig extends Bundle {
    val ext_mode = UInt(3.W) // sign-extention module mode
    val alu_src = UInt(1.W)
    val alu_op = UInt(4.W)
    val carry_write = UInt(1.W)
    val jmp_mode = UInt(3.W)
    val stack_op = UInt(2.W)
    val mem_data_src = UInt(1.W)
    val mem_addr_src = UInt(1.W)
    val mem_read = UInt(1.W)
    val mem_write = UInt(1.W)
    val rd_src = UInt(1.W)
    val reg_write = UInt(1.W)
    val carry_src = UInt(1.W)
    val jmp_amt_src = UInt(1.W)
    val pc_src = UInt(2.W)
}
