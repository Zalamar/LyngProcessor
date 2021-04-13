package lyng.memory

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}
import chisel3.iotesters.Driver


class MemoryTopTester(dut: MemoryTop) extends PeekPokeTester(dut) {
    //No memory operation (ADD)
    poke(dut.io.ctrl.stack_op, 0)
    poke(dut.io.ctrl.mem_write, 0)
    poke(dut.io.ctrl.mem_read, 0)
    poke(dut.io.ctrl.mem_data_src, 0)
    poke(dut.io.ctrl.mem_addr_src, 0)
    poke(dut.io.in.prop_ME_ME, 0)
    poke(dut.io.in.jump_amt, 1)
    poke(dut.io.in.alu_res, 0x1111)
    poke(dut.io.in.rw_value, 0xCCCC)
    poke(dut.io.in.pc, 0x0FFF0)
    poke(dut.io.in.rd, 0xFF00)
    poke(dut.io.in.rw_addr, 3)
    step(1)
    expect(dut.io.out.alu_res, 0x1111)
    expect(dut.io.out.jump, 0x0FFF1)
    expect(dut.io.out.rw_addr, 3)
    //MOVSP
    poke(dut.io.ctrl.stack_op, 1)
    step(1)
    //PUSH
    poke(dut.io.ctrl.stack_op, 2)
    poke(dut.io.ctrl.mem_write, 1)
    poke(dut.io.ctrl.mem_read, 0)
    poke(dut.io.ctrl.mem_data_src, 0)
    poke(dut.io.ctrl.mem_addr_src, 1)
    poke(dut.io.in.rd, 0xFAFA)
    step(1)
    //READ
    expect(dut.io.out.call, 0xFAFA << 1)
    poke(dut.io.ctrl.stack_op, 0)
    poke(dut.io.ctrl.mem_write, 0)
    poke(dut.io.ctrl.mem_read, 1)
    poke(dut.io.ctrl.mem_data_src, 0)
    poke(dut.io.ctrl.mem_addr_src, 0)
    poke(dut.io.in.alu_res, 0xFF00)
    step(1)
    expect(dut.io.out.data_out, 0xFAFA)
    expect(dut.io.out.return_addr, 0xFAFA << 1)

    //PUSH
    poke(dut.io.ctrl.mem_write, 1)
    poke(dut.io.ctrl.mem_read, 0)
    poke(dut.io.ctrl.mem_data_src, 0)
    poke(dut.io.ctrl.mem_addr_src, 1)
    poke(dut.io.ctrl.stack_op, 2)
    poke(dut.io.in.rd, 0xFBFB)
    step(1)
    //POP
    poke(dut.io.ctrl.mem_write, 0)
    poke(dut.io.ctrl.mem_read, 1)
    poke(dut.io.ctrl.stack_op, 3)
    poke(dut.io.ctrl.mem_addr_src, 1)
    step(1)
    expect(dut.io.out.data_out, 0xFAFA)
}


// test spec
class MemoryTopSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
      Driver.execute(Array("--generate-vcd-output", "on"), () => new MemoryTop()) {
          c => new MemoryTopTester(c)
      } should be (true)
  }
}