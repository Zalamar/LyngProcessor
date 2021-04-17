package lyng.decode

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest._

// test class
class ControlUnitTester(dut: ControlUnit) extends PeekPokeTester(dut) {
    poke(dut.io.opcode, 0)
    poke(dut.io.func, 0)
    expect(dut.io.control.ext_mode, 3)
}

// test spec
class ControlUnitSpec extends FlatSpec with Matchers {
    "Tester" should "pass" in {
        chisel3.iotesters.Driver(() => new ControlUnit) { c =>
            new ControlUnitTester(c)
        } should be (true)
    }
}
