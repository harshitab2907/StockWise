#!/usr/bin/env python3
"""
Generates G15-PID-05.pptx from the Chitkara University official template.
Populates all slides according to project specifications for Continuous Evaluation-3.
"""

import os
import zipfile
import xml.etree.ElementTree as ET

TEMPLATE_PATH = "/Users/harshita/.gemini/antigravity/brain/80ee8a01-3dd8-4c25-aa10-5021ea1185eb/.user_uploaded/media_1795527270611_dabd4650.pptx"
OUTPUT_PPTX = "/Users/harshita/Desktop/java project 1/G15-PID-05.pptx"

# Namespaces
P_NS = "http://schemas.openxmlformats.org/presentationml/2006/main"
A_NS = "http://schemas.openxmlformats.org/drawingml/2006/main"
R_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"

def make_run(text, sz=1400, bold=False, italic=False, color="000000"):
    color_xml = f'<a:solidFill><a:srgbClr val="{color}"/></a:solidFill>' if color else ""
    b_attr = ' b="1"' if bold else ' b="0"'
    i_attr = ' i="1"' if italic else ' i="0"'
    return f'<a:r><a:rPr lang="en-US" sz="{sz}"{b_attr}{i_attr} dirty="0">{color_xml}<a:latin typeface="Arial"/></a:rPr><a:t>{text}</a:t></a:r>'

def make_para(runs_xml, align="l", bullet=False, space_before=150, space_after=50, indent=0, mar_l=0):
    algn_attr = f' algn="{align}"' if align else ""
    bu_xml = '<a:buFont typeface="Arial"/><a:buChar char="•"/>' if bullet else '<a:buNone/>'
    indent_attr = f' indent="-228600" marL="285750"' if bullet else (f' indent="{indent}" marL="{mar_l}"' if mar_l else '')
    spc_bef_xml = f'<a:spcBef><a:spcPts val="{space_before}"/></a:spcBef>' if space_before else ""
    spc_aft_xml = f'<a:spcAft><a:spcPts val="{space_after}"/></a:spcAft>' if space_after else ""
    return f'<a:p><a:pPr{algn_attr}{indent_attr}><a:lnSpc><a:spcPct val="115000"/></a:lnSpc>{spc_bef_xml}{spc_aft_xml}{bu_xml}</a:pPr>{runs_xml}</a:p>'

# --- Slide 1 XML ---
slide1_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="{A_NS}" xmlns:r="{R_NS}" xmlns:p="{P_NS}">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name=""/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="3" name="Text Placeholder 2"/>
          <p:cNvSpPr><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="body" idx="1"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="457200" y="700000"/><a:ext cx="8229600" cy="4900000"/></a:xfrm>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {make_para(make_run("PROJECT PRESENTATION", sz=1800, bold=True, color="555555"), align="ctr", space_before=0, space_after=30)}
          {make_para(make_run("Continuous Evaluation-3  |  Programming in JAVA (25CSE0202)", sz=1300, bold=True, color="777777"), align="ctr", space_before=0, space_after=50)}
          {make_para(make_run("on", sz=1300, italic=True, color="555555"), align="ctr", space_before=0, space_after=50)}
          {make_para(make_run("StockWise — Student Inventory Demand Predictor", sz=2400, bold=True, color="C00000"), align="ctr", space_before=0, space_after=30)}
          {make_para(make_run("Tagline: Smart Inventory. Smarter Decisions.   |   Project ID: G15-PID-05", sz=1300, italic=True, bold=True, color="222222"), align="ctr", space_before=0, space_after=50)}
          {make_para(make_run("GitHub Repository: ", sz=1250, bold=True, color="003366") + make_run("https://github.com/harshitab2907/StockWise", sz=1250, bold=True, color="0033CC"), align="ctr", space_before=0, space_after=180)}
          
          {make_para(make_run("Submitted by:                                                              Submitted to:", sz=1400, bold=True, color="000000"), align="l", space_before=100, space_after=50, mar_l=300000)}
          {make_para(make_run("• Harshita (2510990160) [Team Leader]                       Course Faculty / Evaluation Committee", sz=1200, color="222222"), align="l", space_before=20, space_after=20, mar_l=300000)}
          {make_para(make_run("• Janvi (2510990150)                                                       Department of Computer Science &amp; Engineering", sz=1200, color="222222"), align="l", space_before=20, space_after=20, mar_l=300000)}
          {make_para(make_run("• Anmol (2510990843)                                                       Chitkara University, Punjab", sz=1200, color="222222"), align="l", space_before=20, space_after=20, mar_l=300000)}
          {make_para(make_run("• Sidak (2510990488)", sz=1200, color="222222"), align="l", space_before=20, space_after=20, mar_l=300000)}
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="5" name="TextBox 4"/>
          <p:cNvSpPr txBox="1"/><p:nvPr/>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="1500000" y="5850000"/><a:ext cx="6144000" cy="800000"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom>
          <a:noFill/>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {make_para(make_run("Department of Computer Science and Engineering,", sz=1350, bold=True, color="333333"), align="ctr", space_before=0, space_after=20)}
          {make_para(make_run("Chitkara University, Punjab", sz=1350, bold=True, color="333333"), align="ctr", space_before=0, space_after=0)}
        </p:txBody>
      </p:sp>

    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"""

# --- Slide 2 XML (Problem Statement) ---
slide2_body = (
    make_para(make_run("Challenges Faced in College Inventory Management:", sz=1450, bold=True, color="003366"), space_before=0, space_after=60) +
    make_para(make_run("1. Inefficient Manual Tracking: ", sz=1350, bold=True, color="C00000") + 
              make_run("Lab components (Arduino, Raspberry Pi, sensor packs, multimeters) are tracked via registers or spreadsheets, causing data mismatches and lost stock.", sz=1300), bullet=True, space_before=70, space_after=40) +
    make_para(make_run("2. Unpredictable Demand Surges: ", sz=1350, bold=True, color="C00000") + 
              make_run("Student project deadlines and semester lab exams cause sudden demand spikes, resulting in unexpected stockouts and academic project delays.", sz=1300), bullet=True, space_before=70, space_after=40) +
    make_para(make_run("3. Lack of Accountable Issuance: ", sz=1350, bold=True, color="C00000") + 
              make_run("Manual issuing without atomic stock checks leads to duplicate allocations, inventory deficits, and zero student accountability.", sz=1300), bullet=True, space_before=70, space_after=40) +
    make_para(make_run("4. Budget Wastage & Ineffective Restocking: ", sz=1350, bold=True, color="C00000") + 
              make_run("Absence of consumption foresight leads to over-purchasing slow-moving items while high-demand items remain out of stock.", sz=1300), bullet=True, space_before=70, space_after=40) +
    make_para(make_run("5. Need for Explainable Intelligence: ", sz=1350, bold=True, color="C00000") + 
              make_run("Academic audits demand a deterministic, mathematically transparent algorithm rather than opaque black-box machine learning models.", sz=1300), bullet=True, space_before=70, space_after=20)
)

slide2_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="{A_NS}" xmlns:r="{R_NS}" xmlns:p="{P_NS}">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name="Shape 56"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="57" name="Google Shape;57;g37844a4f8ae_15_47"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="title"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="151002" y="140546"/><a:ext cx="6477000" cy="504600"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="12050" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="l"><a:buNone/></a:pPr><a:r><a:rPr lang="en-US" sz="2400" b="1" dirty="0"><a:solidFill><a:srgbClr val="003366"/></a:solidFill></a:rPr><a:t>Problem Statement</a:t></a:r></a:p>
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="58" name="Google Shape;58;g37844a4f8ae_15_47"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="sldNum" idx="12"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="6553200" y="6629400"/><a:ext cx="2133600" cy="186600"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="1900" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="r"><a:buNone/></a:pPr><a:fld id="{{00000000-1234-1234-1234-123412341234}}" type="slidenum"><a:rPr lang="en-US" sz="1100"/><a:t>2</a:t></a:fld></a:p>
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="60" name="Content Body Shape"/>
          <p:cNvSpPr txBox="1"/>
          <p:nvPr/>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="457200" y="1000000"/><a:ext cx="8229600" cy="5400000"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {slide2_body}
        </p:txBody>
      </p:sp>

    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"""

# --- Slide 3 XML (Project Objectives and Scope) ---
slide3_body = (
    make_para(make_run("Key Project Objectives:", sz=1400, bold=True, color="003366"), space_before=0, space_after=40) +
    make_para(make_run("• Modular Core Java Architecture: ", sz=1300, bold=True, color="C00000") +
              make_run("Strictly adheres to OOP, clean layered design, and Inversion of Control via AppContext with 0 external dependencies.", sz=1250), space_before=40, space_after=30, mar_l=200000) +
    make_para(make_run("• Atomic Stock Consistency: ", sz=1300, bold=True, color="C00000") +
              make_run("Deducts stock exactly once upon demand approval; automatically rolls back changes if stock is insufficient.", sz=1250), space_before=40, space_after=30, mar_l=200000) +
    make_para(make_run("• Explainable 3-Month Moving Average: ", sz=1300, bold=True, color="C00000") +
              make_run("Predicted Demand = (Q1 + Q2 + Q3) / 3. Strictly validates coverage; flags INSUFFICIENT_HISTORY if prior months lack data.", sz=1250), space_before=40, space_after=30, mar_l=200000) +
    make_para(make_run("• Suggested Restocking Intelligence: ", sz=1300, bold=True, color="C00000") +
              make_run("Suggested Restock = MAX(0, ceil(Predicted Demand + Safety Stock - Current Stock)). Automatically highlights high-demand risks.", sz=1250), space_before=40, space_after=60, mar_l=200000) +
    make_para(make_run("Project Scope & Evaluation Roadmap:", sz=1400, bold=True, color="003366"), space_before=60, space_after=40) +
    make_para(make_run("• Evaluation 1 (Current): ", sz=1300, bold=True, color="006600") +
              make_run("In-memory Collections, ASCII tables, 28 automated tests (100% pass), complete audit logging.", sz=1250), space_before=40, space_after=30, mar_l=200000) +
    make_para(make_run("• Evaluation 2 (Upcoming): ", sz=1300, bold=True, color="777777") +
              make_run("Seamless swap to MySQL database via JDBC repository contracts without modifying service business logic.", sz=1250), space_before=40, space_after=20, mar_l=200000)
)

slide3_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="{A_NS}" xmlns:r="{R_NS}" xmlns:p="{P_NS}">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name="Shape 72"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="73" name="Google Shape;73;g37844a4f8ae_15_36"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="title"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="151002" y="140546"/><a:ext cx="6477000" cy="517434"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="12050" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="l"><a:buNone/></a:pPr><a:r><a:rPr lang="en-US" sz="2400" b="1" dirty="0"><a:solidFill><a:srgbClr val="003366"/></a:solidFill></a:rPr><a:t>Project Objectives and Scope</a:t></a:r></a:p>
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="74" name="Google Shape;74;g37844a4f8ae_15_36"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="body" idx="1"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="457200" y="950000"/><a:ext cx="8229600" cy="5500000"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {slide3_body}
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="75" name="SlideNum"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="sldNum" idx="12"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="6553200" y="6629400"/><a:ext cx="2133600" cy="186600"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="1900" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="r"><a:buNone/></a:pPr><a:fld id="{{00000000-1234-1234-1234-123412341234}}" type="slidenum"><a:rPr lang="en-US" sz="1100"/><a:t>3</a:t></a:fld></a:p>
        </p:txBody>
      </p:sp>

    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"""

# --- Slide 4 XML (Hardware and Software Interface) ---
slide4_body = (
    make_para(make_run("Hardware Interface Requirements:", sz=1450, bold=True, color="003366"), space_before=0, space_after=40) +
    make_para(make_run("• Client Workstation / Host: ", sz=1300, bold=True, color="C00000") +
              make_run("Standard Desktop or Laptop (macOS, Windows 10/11, or Linux).", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Processor Architecture: ", sz=1300, bold=True, color="C00000") +
              make_run("64-bit multi-core CPU (Intel Core i3/i5/i7, AMD Ryzen, or Apple Silicon M-Series).", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Memory & Disk Storage: ", sz=1300, bold=True, color="C00000") +
              make_run("Minimum 4 GB RAM (8 GB recommended); 200 MB free disk space for JDK runtime.", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Input / Output Peripherals: ", sz=1300, bold=True, color="C00000") +
              make_run("Standard Keyboard and Display Monitor for interactive console terminal operations.", sz=1250), space_before=40, space_after=60, mar_l=200000) +
    
    make_para(make_run("Software Interface & Technology Stack:", sz=1450, bold=True, color="003366"), space_before=60, space_after=40) +
    make_para(make_run("• Operating System: ", sz=1300, bold=True, color="006600") +
              make_run("Cross-platform compatible (macOS, Windows, Linux via standard Java bytecode).", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Core Language & Runtime: ", sz=1300, bold=True, color="006600") +
              make_run("Java SE 17 LTS (Core Java, java.time, Collections API; Strictly 0 external libraries).", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Data Persistence Layer: ", sz=1300, bold=True, color="006600") +
              make_run("In-Memory thread-safe Java Collections (LinkedHashMap, ArrayList, TreeMap).", sz=1250), space_before=40, space_after=25, mar_l=200000) +
    make_para(make_run("• Automation & Version Control: ", sz=1300, bold=True, color="006600") +
              make_run("Git & GitHub repository (https://github.com/harshitab2907/StockWise); Shell test runner.", sz=1250), space_before=40, space_after=20, mar_l=200000)
)

slide4_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="{A_NS}" xmlns:r="{R_NS}" xmlns:p="{P_NS}">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name="Shape 99"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="100" name="Google Shape;100;g37844a4f8ae_15_18"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="title"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="151002" y="140546"/><a:ext cx="6477000" cy="517434"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="12050" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="l"><a:buNone/></a:pPr><a:r><a:rPr lang="en-US" sz="2400" b="1" dirty="0"><a:solidFill><a:srgbClr val="003366"/></a:solidFill></a:rPr><a:t>Hardware and Software Interface</a:t></a:r></a:p>
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="102" name="Content Body Shape"/>
          <p:cNvSpPr txBox="1"/>
          <p:nvPr/>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="457200" y="950000"/><a:ext cx="8229600" cy="5500000"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {slide4_body}
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="101" name="Google Shape;101;g37844a4f8ae_15_18"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="sldNum" idx="12"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="6553200" y="6629400"/><a:ext cx="2133600" cy="186600"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="1900" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="r"><a:buNone/></a:pPr><a:fld id="{{00000000-1234-1234-1234-123412341234}}" type="slidenum"><a:rPr lang="en-US" sz="1100"/><a:t>4</a:t></a:fld></a:p>
        </p:txBody>
      </p:sp>

    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"""

# --- Slide 5 XML (Thank You) ---
slide5_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="{A_NS}" xmlns:r="{R_NS}" xmlns:p="{P_NS}">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr><p:cNvPr id="1" name="Shape 229"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>
      <p:grpSpPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="0" cy="0"/><a:chOff x="0" y="0"/><a:chExt cx="0" cy="0"/></a:xfrm></p:grpSpPr>
      
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="233" name="Google Shape;233;p43"/>
          <p:cNvSpPr txBox="1"><a:spLocks noGrp="1"/></p:cNvSpPr>
          <p:nvPr><p:ph type="sldNum" idx="12"/></p:nvPr>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="6553200" y="6583684"/><a:ext cx="2133600" cy="274316"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/><a:ln><a:noFill/></a:ln>
        </p:spPr>
        <p:txBody>
          <a:bodyPr spcFirstLastPara="1" wrap="square" lIns="0" tIns="1900" rIns="0" bIns="0" anchor="ctr"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          <a:p><a:pPr marL="12700" indent="0" algn="r"><a:buNone/></a:pPr><a:fld id="{{00000000-1234-1234-1234-123412341234}}" type="slidenum"><a:rPr lang="en-US" sz="1100"/><a:t>5</a:t></a:fld></a:p>
        </p:txBody>
      </p:sp>

      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="3" name="TextBox 2"/>
          <p:cNvSpPr txBox="1"/><p:nvPr/>
        </p:nvSpPr>
        <p:spPr>
          <a:xfrm><a:off x="1000000" y="1800000"/><a:ext cx="7144000" cy="4000000"/></a:xfrm>
          <a:prstGeom prst="rect"><a:avLst/></a:prstGeom><a:noFill/>
        </p:spPr>
        <p:txBody>
          <a:bodyPr wrap="square"><a:spAutoFit/></a:bodyPr>
          <a:lstStyle/>
          {make_para(make_run("Thank You", sz=3600, bold=True, color="C00000"), align="ctr", space_before=0, space_after=60)}
          {make_para(make_run("Questions &amp; Answers  /  Viva Discussion", sz=1800, bold=True, color="003366"), align="ctr", space_before=30, space_after=100)}
          {make_para(make_run("StockWise — Student Inventory Demand Predictor", sz=1600, bold=True, color="222222"), align="ctr", space_before=20, space_after=20)}
          {make_para(make_run("Smart Inventory. Smarter Decisions.", sz=1300, italic=True, color="555555"), align="ctr", space_before=10, space_after=150)}
          {make_para(make_run("Project ID: G15-PID-05", sz=1400, bold=True, color="C00000"), align="ctr", space_before=20, space_after=50)}
          {make_para(make_run("Team Leader: Harshita (2510990160)", sz=1300, bold=True, color="333333"), align="ctr", space_before=10, space_after=20)}
          {make_para(make_run("Team Members: Janvi (2510990150) | Anmol (2510990843) | Sidak (2510990488)", sz=1200, color="555555"), align="ctr", space_before=10, space_after=50)}
          {make_para(make_run("Department of Computer Science and Engineering, Chitkara University, Punjab", sz=1250, bold=True, color="333333"), align="ctr", space_before=20, space_after=50)}
          {make_para(make_run("GitHub Repository: ", sz=1250, bold=True, color="003366") + make_run("https://github.com/harshitab2907/StockWise", sz=1250, bold=True, color="0033CC"), align="ctr", space_before=20, space_after=0)}
        </p:txBody>
      </p:sp>

    </p:spTree>
  </p:cSld>
  <p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>
</p:sld>
"""

# Rebuild PPTX zip archive
slide_replacements = {
    "ppt/slides/slide1.xml": slide1_xml,
    "ppt/slides/slide2.xml": slide2_xml,
    "ppt/slides/slide3.xml": slide3_xml,
    "ppt/slides/slide4.xml": slide4_xml,
    "ppt/slides/slide5.xml": slide5_xml,
}

with zipfile.ZipFile(TEMPLATE_PATH, "r") as zin:
    with zipfile.ZipFile(OUTPUT_PPTX, "w", zipfile.ZIP_DEFLATED) as zout:
        for item in zin.infolist():
            if item.filename in slide_replacements:
                zout.writestr(item.filename, slide_replacements[item.filename].encode("utf-8"))
            else:
                zout.writestr(item.filename, zin.read(item.filename))

print(f"Generated successfully: {OUTPUT_PPTX} ({os.path.getsize(OUTPUT_PPTX)} bytes)")
