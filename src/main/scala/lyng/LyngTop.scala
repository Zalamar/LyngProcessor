package lyng

import chisel3._
import chisel3.util._
import lyng.memory.MemoryTop
import lyng.decode.DecodeTop
import lyng.execute.ExecuteTop


class IF_IDSig extends Bundle {
    val pc = UInt(16.W)
}

class ID_EXSig extends Bundle {
    val ctrl = new ControlUnitSig

    //Inputs of EX stage
    val rs1 = UInt(16.W)
    val rs2 = UInt(16.W)
    val imm = UInt(11.W)

    //Other values
    val pc = UInt(16.W)
    val rd = UInt(16.W)
    val rs1_addr = UInt(3.W)
    val rs2_addr = UInt(3.W)
    val rw_addr = UInt(3.W)

}

class EX_MESig extends Bundle {

    val ctrl = new ControlUnitSig

    val pc = UInt(16.W)
    val jump = UInt(1.W)
    val jump_amt = UInt(16.W)
    val alu_res = UInt(16.W)
    val rd = UInt(16.W)
    val rw_addr = UInt(3.W)

}


class ME_WBSig extends Bundle {

    val ctrl = new ControlUnitSig

    val jump = UInt(1.W)
    val rw_addr = UInt(3.W)
    val alu_res = UInt(16.W)
}

class LyngTop extends Module {
    val io = IO(new Bundle{
        val load = Input(UInt(2.W))
        val addr = Input(UInt(16.W))
        val value = Input(UInt(16.W))
        val out_valid = Output(UInt(1.W))
        val out = Output(UInt(16.W))
    })

    val out_addr = "hFFFE".U

    val instr_mem = Module(new Memory(16, 16))
    val data_mem = Module(new Memory(16, 16))

    //Pipeline interstage register instantiation
    val pc = Module(new PipelineReg(() => UInt(16.W)))
    val if_id = Module(new PipelineReg(() => new IF_IDSig))
    val id_ex = Module(new PipelineReg(() => new ID_EXSig))
    val ex_me = Module(new PipelineReg(() => new EX_MESig))
    val me_wb = Module(new PipelineReg(() => new ME_WBSig))

    //Pipeline stage declaration
    val id = Module(new DecodeTop)
    val ex = Module(new ExecuteTop)
    val me = Module(new MemoryTop)

    //WB stage declarations
    val rw_value = Wire(UInt(16.W))

    //Stall and Propagation Units
    val ex_rs1_unit = Module(new EXPropagationUnit)
    val ex_rs2_unit = Module(new EXPropagationUnit)
    val me_unit = Module(new MEPropagationUnit)
    val stall = Module(new StallUnit)

    /*
    *    IF Stage
    */
    //IF next_pc calculation
    val next_pc = Wire(UInt(16.W))
    val curr_pc = Wire(UInt(16.W))
    pc.io.in := next_pc

    //Default Values
    next_pc := pc.io.out + 1.U
    curr_pc := pc.io.out
    //Branch in ME Stage
    when(ex_me.io.out.jump === 1.U) {
        switch(ex_me.io.out.ctrl.pc_src) {
            is("b00".U) {
                next_pc := curr_pc + 1.U
            }
            is("b01".U) {
                next_pc := me.io.out.jump + 1.U
            }
            is("b10".U) {
                next_pc := me.io.out.call
            } /*
            is("b11".U) {
                next_pc := me.io.out.return_addr + 1.U
            } */
        }
    }

    //Branch in WB State (with PC OVERRIDE)
    when(me_wb.io.out.ctrl.pc_src === "b11".U && me_wb.io.out.jump === 1.U) {
      curr_pc := me.io.out.return_addr + 1.U
      next_pc := me.io.out.return_addr + 2.U
    }


    instr_mem.io.mem_read  := 1.U
    instr_mem.io.mem_write := 0.U
    instr_mem.io.addr := curr_pc
    when(io.load === "b01".U) {
        instr_mem.io.mem_read  := 0.U
        instr_mem.io.mem_write := 1.U
        instr_mem.io.addr := io.addr
    }
    instr_mem.io.data_in := io.value

    if_id.io.in.pc := curr_pc


    /**
      * ID Stage
      */
    //IF_ID outs -> ID inputs
    id.io.in.instr := instr_mem.io.data_out
    switch(RegNext(stall.io.if_id_mode)) {
      is("b10".U) {
        id.io.in.instr := 0.U
      }
      is("b01".U) {
        id.io.in.instr := RegNext(instr_mem.io.data_out)
      }
    }
    //WB -> register file out
    id.io.in.rw_addr := me_wb.io.out.rw_addr
    id.io.in.wb_rw_value := rw_value
    id.io.in.wb_reg_write := me_wb.io.out.ctrl.reg_write
    //ID_EX value
    id_ex.io.in.ctrl := id.io.out.ctrl
    id_ex.io.in.rs1 := id.io.out.rs1
    id_ex.io.in.rs2 := id.io.out.rs2
    id_ex.io.in.imm := id.io.out.imm
    id_ex.io.in.pc := if_id.io.out.pc
    id_ex.io.in.rd := id.io.out.rd
    id_ex.io.in.rs1_addr := id.io.out.rs1_addr
    id_ex.io.in.rs2_addr := id.io.out.rs2_addr
    id_ex.io.in.rw_addr :=  id.io.out.rd_addr

    /**
      * EX Stage
      */
    //ID/EX Outputs -> EX Inputs
    ex.io.ctrl := id_ex.io.out.ctrl
    ex.io.in.rs1 := id_ex.io.out.rs1
    ex.io.in.rs2 := id_ex.io.out.rs2
    ex.io.in.imm := id_ex.io.out.imm
    //Propagation inputs
    ex.io.in.prop_rs1 := ex_rs1_unit.io.prop_rx
    ex.io.in.prop_rs2 := ex_rs2_unit.io.prop_rx
    ex.io.in.ex_forward := ex_me.io.out.alu_res
    ex.io.in.me_forward := rw_value
    //EX Outputs -> EX/ME Inputs

    ex_me.io.in.jump := ex.io.out.jump
    ex_me.io.in.jump_amt := ex.io.out.jump_amt
    ex_me.io.in.alu_res := ex.io.out.alu_res
    //ID/EX -> EX/ME Direct connections
    ex_me.io.in.ctrl := id_ex.io.out.ctrl
    ex_me.io.in.pc := id_ex.io.out.pc
    ex_me.io.in.rw_addr := id_ex.io.out.rw_addr
    ex_me.io.in.rd := id_ex.io.out.rd



    /**
      * ME Stage
      */
    me.io.ctrl := ex_me.io.out.ctrl
    //EX_ME outputs -> ME inputs
    me.io.in.jump := ex_me.io.out.jump
    me.io.in.jump_amt := ex_me.io.out.jump_amt
    me.io.in.alu_res := ex_me.io.out.alu_res
    me.io.in.pc := ex_me.io.out.pc
    me.io.in.rd := ex_me.io.out.rd
    me.io.in.rw_addr := ex_me.io.out.rw_addr
    //Other

    //ME outputs -> Data Memory inputs
    data_mem.io.data_in := me.io.out.data_in
    data_mem.io.addr := me.io.out.addr
    data_mem.io.mem_write := ex_me.io.out.ctrl.mem_write
    data_mem.io.mem_read := ex_me.io.out.ctrl.mem_read
    //Data Memory -> ME inputs
    me.io.in.data_out := data_mem.io.data_out

    //ME Outputs -> ME_WB inputs
    me_wb.io.in.ctrl := ex_me.io.out.ctrl
    me_wb.io.in.rw_addr := me.io.out.rw_addr
    me_wb.io.in.alu_res := me.io.out.alu_res
    me_wb.io.in.jump := ex_me.io.out.jump //Directly from EX_ME

    //Propagation
    me.io.in.prop_ME_ME := me_unit.io.prop_ME_ME
    me.io.in.rw_value := rw_value

    /**
      * WB Stage
      */
    rw_value := Mux(me_wb.io.out.ctrl.rd_src === true.B,
        me.io.out.data_out, //1
        me_wb.io.out.alu_res   //0
    )

    /**
      * Propagation Units
      */
    //Rs1 EX->EX ME->EX
    ex_rs1_unit.io.ex_me.reg_write := ex_me.io.out.ctrl.reg_write
    ex_rs1_unit.io.ex_me.rw_addr := ex_me.io.out.rw_addr
    ex_rs1_unit.io.me_wb.reg_write := me_wb.io.out.ctrl.reg_write
    ex_rs1_unit.io.me_wb.rw_addr := me_wb.io.out.rw_addr
    ex_rs1_unit.io.ex_me_mem_read := ex_me.io.out.ctrl.mem_read
    ex_rs1_unit.io.rx_addr := id_ex.io.out.rs1_addr
    //Rs EX->EX ME->EX
    ex_rs2_unit.io.ex_me.reg_write := ex_me.io.out.ctrl.reg_write
    ex_rs2_unit.io.ex_me.rw_addr := ex_me.io.out.rw_addr
    ex_rs2_unit.io.me_wb.reg_write := me_wb.io.out.ctrl.reg_write
    ex_rs2_unit.io.me_wb.rw_addr := me_wb.io.out.rw_addr
    ex_rs2_unit.io.ex_me_mem_read := ex_me.io.out.ctrl.mem_read
    ex_rs2_unit.io.rx_addr := id_ex.io.out.rs2_addr
    //ME->ME
    me_unit.io.me_wb_reg_write := me_wb.io.out.ctrl.reg_write
    me_unit.io.me_wb_rw_addr := me_wb.io.out.rw_addr
    me_unit.io.ex_me_rw_addr := ex_me.io.out.rw_addr

    /**
      * STALL UNIT
      */
    stall.io.ex_jump := ex.io.out.jump
    stall.io.ex_me_jump := ex_me.io.out.jump
    stall.io.instr_mem_read := 1.U
    stall.io.instr_mem_valid := instr_mem.io.valid
    stall.io.data_mem_read := 1.U //ex_me.io.out.ctrl.mem_read
    stall.io.data_mem_valid := data_mem.io.valid
    stall.io.conflict_stall := ex_rs1_unit.io.conflict_stall | ex_rs2_unit.io.conflict_stall
    stall.io.load_mode := (io.load === "b01".U).asUInt()
    //Stall unit out (-> Pipeline Registers control)
    pc.io.ctrl := Mux(io.load === "b01".U, "b10".U, stall.io.pc_mode)
    if_id.io.ctrl := stall.io.if_id_mode
    id_ex.io.ctrl := stall.io.id_ex_mode
    ex_me.io.ctrl := stall.io.ex_me_mode
    me_wb.io.ctrl := stall.io.me_wb_mode

    //Output
    io.out_valid := ex_me.io.out.ctrl.mem_write === 1.U && ex_me.io.ctrl === "b00".U && me.io.out.addr === out_addr
    io.out := me.io.out.data_in

}

object LyngTop extends App {
  println("Generating Lyng Top Module")
  (new chisel3.stage.ChiselStage).emitVerilog(new LyngTop(), Array("--target-dir", "generated"))
}
