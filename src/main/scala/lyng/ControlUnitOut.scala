
package lyng

import chisel3._
import chisel3.util._

class ControlUnitOut extends Bundle {
    val ext_mode = Output(UInt(3.W))
    val alu_src = Output(UInt(1.W))
    val alu_op = Output(UInt(4.W))
    val carry_write = Output(UInt(1.W))
    val jmp_mode = Output(UInt(3.W))
    val stack_op = Output(UInt(2.W))
    val mem_data_src = Output(UInt(1.W))
    val mem_addr_src = Output(UInt(1.W))
    val mem_read = Output(UInt(1.W))
    val mem_write = Output(UInt(1.W))
    val rd_src = Output(UInt(1.W))
}