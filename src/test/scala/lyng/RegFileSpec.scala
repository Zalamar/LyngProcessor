package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest._

// test class
class RegFileTester(dut: RegFile) extends PeekPokeTester(dut) {
    val testVals = Array(0x0, 0xF, 0x123, 0xFFFF, 0x1, 0x2, 0x3, 0x4)
    // test write in
    poke(dut.io.reg_write, 1)
    for (i <- 0 until testVals.length) {
        poke(dut.io.rd_in, testVals(i))
        poke(dut.io.rd_addr, i)
        step(1)
    }
    poke(dut.io.reg_write, 0)
    // test write out
    for (i <- 0 until 7) {
       poke(dut.io.rs1_addr, i)
       step(1)
       expect(dut.io.rs1_out, testVals(i))
    }
    for (i <- 0 until 7) {
       poke(dut.io.rs2_addr, i)
       step(1)
       expect(dut.io.rs2_out, testVals(i))
    }
    for (i <- 0 until 7) {
       poke(dut.io.rd_addr, i)
       step(1)
       expect(dut.io.rd_out, testVals(i))
    }
}

// test spec
class RegFileSpec extends FlatSpec with Matchers {
    "Tester" should "pass" in {
        chisel3.iotesters.Driver (() => new RegFile()) { c =>
            new RegFileTester(c)
        } should be (true)
    }
}
