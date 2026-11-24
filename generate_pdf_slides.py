#!/usr/bin/env python3
"""
Renders the 5-slide academic presentation to HTML with exact Chitkara University
template dimensions (4:3 aspect ratio, 10in x 7.5in) and exports to G15-PID-05.pdf.
"""

import os
import subprocess
import base64

WORKSPACE = "/Users/harshita/Desktop/java project 1"

def get_base64_image(filename):
    path = os.path.join(WORKSPACE, filename)
    if os.path.exists(path):
        with open(path, "rb") as f:
            b64 = base64.b64encode(f.read()).decode("utf-8")
            ext = filename.split(".")[-1]
            return f"data:image/{ext};base64,{b64}"
    return ""

img1_b64 = get_base64_image("image1.png")
img2_b64 = get_base64_image("image2.jpg")

html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<style>
  @page {{
    size: 10in 7.5in;
    margin: 0;
  }}
  * {{
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }}
  body {{
    font-family: 'Arial', sans-serif;
    color: #222;
    background: #e0e0e0;
    -webkit-print-color-adjust: exact;
  }}
  .slide {{
    width: 10in;
    height: 7.5in;
    position: relative;
    page-break-after: always;
    background: #ffffff;
    overflow: hidden;
    padding: 0.5in 0.7in;
  }}
  .top-logo {{
    position: absolute;
    top: 0.3in;
    right: 0.6in;
    height: 0.75in;
  }}
  .slide-header {{
    position: absolute;
    top: 0.35in;
    left: 0.7in;
    font-size: 26pt;
    font-weight: bold;
    color: #003366;
    border-bottom: 3px solid #C00000;
    padding-bottom: 6px;
    width: 6.2in;
  }}
  .slide-num {{
    position: absolute;
    bottom: 0.3in;
    right: 0.7in;
    font-size: 13pt;
    color: #888;
  }}
  .slide-content {{
    position: absolute;
    top: 1.4in;
    left: 0.7in;
    width: 8.6in;
    height: 5.5in;
  }}
  
  /* Slide 1 styles */
  .title-box {{
    text-align: center;
    margin-top: 0.4in;
  }}
  .pres-badge {{
    font-size: 18pt;
    font-weight: bold;
    color: #555;
    letter-spacing: 1px;
    margin-bottom: 6px;
  }}
  .eval-badge {{
    font-size: 13pt;
    font-weight: 600;
    color: #777;
    margin-bottom: 14px;
  }}
  .on-text {{
    font-size: 13pt;
    font-style: italic;
    color: #555;
    margin-bottom: 12px;
  }}
  .proj-title {{
    font-size: 27pt;
    font-weight: 900;
    color: #C00000;
    margin-bottom: 10px;
    line-height: 1.2;
  }}
  .proj-subtitle {{
    font-size: 14pt;
    font-style: italic;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
  }}
  .repo-link {{
    font-size: 13pt;
    font-weight: bold;
    color: #003366;
    margin-bottom: 24px;
  }}
  .repo-link a {{
    color: #0044cc;
    text-decoration: underline;
  }}
  .members-grid {{
    display: flex;
    justify-content: space-between;
    margin-top: 0.25in;
    padding: 0 0.3in;
    font-size: 12.5pt;
    line-height: 1.6;
  }}
  .col-sub {{
    flex: 1;
    text-align: left;
  }}
  .col-fac {{
    flex: 1;
    text-align: right;
  }}
  .sub-head {{
    font-size: 14pt;
    font-weight: bold;
    color: #000;
    margin-bottom: 6px;
    text-decoration: underline;
  }}
  .uni-footer {{
    position: absolute;
    bottom: 0.35in;
    left: 0;
    width: 100%;
    text-align: center;
    font-size: 13pt;
    font-weight: bold;
    color: #333;
    line-height: 1.4;
  }}

  /* Content slides */
  .section-heading {{
    font-size: 15pt;
    font-weight: bold;
    color: #003366;
    margin-bottom: 8px;
  }}
  .bullet-list {{
    list-style: none;
    margin-bottom: 16px;
  }}
  .bullet-list li {{
    position: relative;
    padding-left: 24px;
    margin-bottom: 9px;
    font-size: 12.5pt;
    line-height: 1.42;
    color: #222;
  }}
  .bullet-list li::before {{
    content: "•";
    position: absolute;
    left: 4px;
    color: #C00000;
    font-size: 16pt;
    top: -2px;
  }}
  .highlight {{
    font-weight: bold;
    color: #C00000;
  }}
  .code-term {{
    font-family: 'Courier New', monospace;
    background: #f0f4f8;
    padding: 2px 5px;
    border-radius: 3px;
    color: #003366;
    font-weight: bold;
  }}
  
  /* Slide 5 Thank You */
  .ty-box {{
    text-align: center;
    margin-top: 0.6in;
  }}
  .ty-title {{
    font-size: 38pt;
    font-weight: 900;
    color: #C00000;
    margin-bottom: 12px;
  }}
  .ty-subtitle {{
    font-size: 20pt;
    font-weight: bold;
    color: #003366;
    margin-bottom: 24px;
  }}
  .ty-info {{
    font-size: 14pt;
    line-height: 1.8;
    color: #333;
  }}
</style>
</head>
<body>

<!-- SLIDE 1 -->
<div class="slide">
  {'<img src="' + img1_b64 + '" class="top-logo">' if img1_b64 else ""}
  <div class="title-box">
    <div class="pres-badge">PROJECT PRESENTATION</div>
    <div class="eval-badge">Continuous Evaluation-3 &nbsp;|&nbsp; Programming in JAVA (25CSE0202)</div>
    <div class="on-text">on</div>
    <div class="proj-title">StockWise — Student Inventory Demand Predictor</div>
    <div class="proj-subtitle">Smart Inventory. Smarter Decisions. &nbsp;|&nbsp; Project ID: <span style="color:#C00000;">G15-PID-05</span></div>
    <div class="repo-link">GitHub Repository: <a href="https://github.com/harshitab2907/StockWise">https://github.com/harshitab2907/StockWise</a></div>
  </div>

  <div class="members-grid">
    <div class="col-sub">
      <div class="sub-head">Submitted by:</div>
      <div>• <b>Harshita</b> (2510990160) <span style="color:#C00000;">[Team Leader]</span></div>
      <div>• <b>Janvi</b> (2510990150)</div>
      <div>• <b>Anmol</b> (2510990843)</div>
      <div>• <b>Sidak</b> (2510990488)</div>
    </div>
    <div class="col-fac">
      <div class="sub-head">Submitted to:</div>
      <div>Course Faculty / Evaluation Committee</div>
      <div>Department of Computer Science and Engineering</div>
      <div>Chitkara University, Punjab</div>
    </div>
  </div>

  <div class="uni-footer">
    Department of Computer Science and Engineering,<br>
    Chitkara University, Punjab
  </div>
</div>

<!-- SLIDE 2 -->
<div class="slide">
  {'<img src="' + img1_b64 + '" class="top-logo">' if img1_b64 else ""}
  <div class="slide-header">Problem Statement</div>
  <div class="slide-num">2</div>

  <div class="slide-content">
    <div class="section-heading">Key Challenges in College Inventory Management:</div>
    <ul class="bullet-list">
      <li><span class="highlight">Inefficient Manual Tracking:</span> College hardware components (Arduino, Raspberry Pi, sensors, multimeters) are traditionally logged using paper registers or disconnected spreadsheets, causing records to desync and items to go missing.</li>
      <li><span class="highlight">Unpredictable Demand Surges:</span> Student requests surge heavily right before semester practical exams, lab assessments, and hackathons, leading to sudden stockouts that disrupt academic coursework.</li>
      <li><span class="highlight">Lack of Accountable Stock Issuance:</span> Equipment is issued without automated real-time verification, resulting in double-allocations, unrecorded deficits, and zero accountability.</li>
      <li><span class="highlight">Budget Wastage & Ineffective Restocking:</span> Absence of consumption foresight leads to over-purchasing slow-moving items while high-demand components remain critically out of stock.</li>
      <li><span class="highlight">Need for Explainable Intelligence:</span> Academic faculty and lab auditors require a deterministic, mathematically transparent forecasting model rather than an opaque black-box machine learning system.</li>
    </ul>
  </div>
</div>

<!-- SLIDE 3 -->
<div class="slide">
  {'<img src="' + img1_b64 + '" class="top-logo">' if img1_b64 else ""}
  <div class="slide-header">Project Objectives and Scope</div>
  <div class="slide-num">3</div>

  <div class="slide-content">
    <div class="section-heading">Core Project Objectives:</div>
    <ul class="bullet-list">
      <li><span class="highlight">Modular Core Java Architecture:</span> Engineered using Java 17 LTS, layered architecture, OOP encapsulation, and Inversion of Control via <span class="code-term">AppContext</span> with <b>zero external dependencies</b>.</li>
      <li><span class="highlight">Atomic Stock Consistency:</span> Guarantees stock is deducted exactly once upon request approval; prevents negative inventory and rolls back state if stock is insufficient.</li>
      <li><span class="highlight">Explainable 3-Month Moving Average Predictor:</span> Calculates <span class="code-term">Predicted Demand = (Q1 + Q2 + Q3) / 3</span>. Strictly verifies monthly coverage and marks missing data as <span class="code-term">INSUFFICIENT_HISTORY</span> without guessing.</li>
      <li><span class="highlight">Smart Restocking Intelligence:</span> Computes <span class="code-term">MAX(0, ceil(Predicted + Safety - Current))</span> and automatically triggers high-demand warnings when predicted consumption exceeds stock.</li>
    </ul>

    <div class="section-heading" style="margin-top:14px;">Project Scope & Evaluation Roadmap:</div>
    <ul class="bullet-list">
      <li><span style="color:#006600; font-weight:bold;">Evaluation 1 (Current Scope):</span> In-memory collection storage, interactive console UI, 28 automated tests (100% pass), and complete audit logging.</li>
      <li><span style="color:#777; font-weight:bold;">Evaluation 2 (Upcoming Milestone):</span> Seamless database persistence via MySQL & JDBC using existing repository interfaces without changing business logic.</li>
    </ul>
  </div>
</div>

<!-- SLIDE 4 -->
<div class="slide">
  {'<img src="' + img1_b64 + '" class="top-logo">' if img1_b64 else ""}
  <div class="slide-header">Hardware and Software Interface</div>
  <div class="slide-num">4</div>

  <div class="slide-content">
    <div class="section-heading">Hardware Interface Requirements:</div>
    <ul class="bullet-list">
      <li><span class="highlight">Host / Client Platform:</span> Standard Laptop or Desktop PC (macOS, Windows 10/11, or Linux).</li>
      <li><span class="highlight">Processor Architecture:</span> 64-bit multi-core CPU (Intel Core i3/i5/i7, AMD Ryzen, or Apple Silicon M-Series).</li>
      <li><span class="highlight">Memory & Storage:</span> Minimum 4 GB RAM (8 GB recommended); 200 MB free disk space for JDK runtime.</li>
      <li><span class="highlight">Input / Output Devices:</span> Standard Keyboard and Display Monitor for interactive console terminal operations.</li>
    </ul>

    <div class="section-heading" style="margin-top:14px;">Software Interface & Technology Stack:</div>
    <ul class="bullet-list">
      <li><span style="color:#006600; font-weight:bold;">Operating System:</span> Platform-independent bytecode execution across macOS, Windows, and Linux.</li>
      <li><span style="color:#006600; font-weight:bold;">Programming Language:</span> Java SE 17 LTS (Core Java, standard runtime library only; 0 external JARs).</li>
      <li><span style="color:#006600; font-weight:bold;">Data Persistence Layer:</span> In-Memory thread-safe Java Collections (<span class="code-term">LinkedHashMap</span>, <span class="code-term">ArrayList</span>, <span class="code-term">TreeMap</span>).</li>
      <li><span style="color:#006600; font-weight:bold;">Version Control & Automation:</span> Git & GitHub repository, Unix automation scripts (<span class="code-term">compile.sh</span>, <span class="code-term">run.sh</span>, <span class="code-term">test.sh</span>).</li>
      <li><span style="color:#006600; font-weight:bold;">Automated Test Suite:</span> Zero-dependency custom assertion runner (<span class="code-term">TestRunner</span>, 28/28 tests passing).</li>
    </ul>
  </div>
</div>

<!-- SLIDE 5 -->
<div class="slide">
  {'<img src="' + img1_b64 + '" class="top-logo">' if img1_b64 else ""}
  <div class="slide-num">5</div>

  <div class="ty-box">
    <div class="ty-title">Thank You</div>
    <div class="ty-subtitle">Questions &amp; Answers &nbsp;/&nbsp; Viva Discussion</div>
    
    <div class="ty-info">
      <div style="font-size:16pt; font-weight:bold; color:#111; margin-bottom:4px;">StockWise — Student Inventory Demand Predictor</div>
      <div style="font-size:13pt; font-style:italic; color:#666; margin-bottom:14px;">Smart Inventory. Smarter Decisions.</div>
      
      <div style="font-size:14pt; font-weight:bold; color:#C00000; margin-bottom:8px;">Project ID: G15-PID-05</div>
      <div style="margin-bottom:4px;"><b>Team Leader:</b> Harshita (2510990160)</div>
      <div style="color:#555; margin-bottom:14px;"><b>Team Members:</b> Janvi (2510990150) &nbsp;|&nbsp; Anmol (2510990843) &nbsp;|&nbsp; Sidak (2510990488)</div>
      
      <div style="font-weight:bold; color:#222; margin-bottom:10px;">Department of Computer Science and Engineering, Chitkara University, Punjab</div>
      <div style="font-size:13pt; font-weight:bold; color:#003366;">
        GitHub: <a href="https://github.com/harshitab2907/StockWise" style="color:#0044cc;">https://github.com/harshitab2907/StockWise</a>
      </div>
    </div>
  </div>
</div>

</body>
</html>
"""

html_path = os.path.join(WORKSPACE, "slides.html")
with open(html_path, "w", encoding="utf-8") as f:
    f.write(html_content)

print(f"Generated {html_path}")

pdf_output = os.path.join(WORKSPACE, "G15-PID-05.pdf")
cmd = [
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "--headless",
    "--disable-gpu",
    "--no-sandbox",
    f"--user-data-dir=/tmp/chrome_pdf_user_{os.getpid()}",
    f"--print-to-pdf={pdf_output}",
    "--print-to-pdf-no-header",
    html_path
]

print("Converting to PDF via Chrome headless...")
res = subprocess.run(cmd, capture_output=True, text=True)
if os.path.exists(pdf_output):
    print(f"SUCCESS: Generated {pdf_output} ({os.path.getsize(pdf_output)} bytes)")
else:
    print(f"FAILED to generate PDF: {res.stderr}")
