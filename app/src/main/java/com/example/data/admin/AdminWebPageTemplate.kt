package com.example.data.admin

object AdminWebPageTemplate {

    fun getHtml(): String {
        return (HTML_HEAD + HTML_BODY_AUTH + HTML_BODY_APP + HTML_MODALS + JS_AUTH + JS_APP + HTML_TAIL).replace("@@", "$")
    }

    private const val HTML_HEAD = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Official 1 — Admin Panel</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-base: #070A0F;
            --bg-surface: #0E131F;
            --bg-card: #131A2B;
            --bg-card-hover: #1C263D;
            --border-color: rgba(255, 255, 255, 0.08);
            --border-accent: rgba(255, 42, 133, 0.35);
            --primary: #FF2A85;
            --primary-glow: rgba(255, 42, 133, 0.25);
            --gold: #FFD700;
            --cyan: #00E5FF;
            --emerald: #00E676;
            --danger: #FF1744;
            --warning: #FF9100;
            --purple: #C084FC;
            --text-primary: #FFFFFF;
            --text-secondary: #94A3B8;
            --text-muted: #64748B;
            --radius-sm: 8px;
            --radius-md: 14px;
            --radius-lg: 20px;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Plus Jakarta Sans', sans-serif; -webkit-tap-highlight-color: transparent; }
        body { background-color: var(--bg-base); color: var(--text-primary); min-height: 100vh; overflow-x: hidden; display: flex; flex-direction: column; }
        .glass-panel { background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); backdrop-filter: blur(12px); }
        .glass-card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-md); transition: all 0.2s ease; }
        .glass-card:hover { border-color: var(--border-accent); box-shadow: 0 8px 24px rgba(0,0,0,0.4); }
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: var(--bg-base); }
        ::-webkit-scrollbar-thumb { background: #2A364F; border-radius: 4px; }
        .auth-screen { display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; background: radial-gradient(circle at top, #1E1538 0%, var(--bg-base) 75%); }
        .auth-box { width: 100%; max-width: 480px; padding: 36px 30px; background: var(--bg-surface); border: 1px solid rgba(255, 42, 133, 0.25); border-radius: var(--radius-lg); box-shadow: 0 20px 50px rgba(0, 0, 0, 0.7); text-align: center; animation: fadeIn 0.3s ease; }
        .brand-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; background: rgba(255, 215, 0, 0.12); border: 1px solid rgba(255, 215, 0, 0.35); border-radius: 999px; color: var(--gold); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 16px; }
        .form-group { text-align: left; margin-bottom: 14px; }
        .form-label { display: block; font-size: 11.5px; font-weight: 700; color: var(--text-secondary); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
        .form-input { width: 100%; padding: 11px 14px; background: #151C2C; border: 1px solid var(--border-color); border-radius: var(--radius-sm); color: #FFFFFF; font-size: 13.5px; outline: none; transition: all 0.2s ease; }
        .form-input:focus { border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-glow); }
        .otp-input-wrap { display: flex; justify-content: center; margin: 18px 0; }
        .otp-large-input { width: 100%; max-width: 280px; font-family: 'JetBrains Mono', monospace; font-size: 26px; font-weight: 800; letter-spacing: 10px; text-align: center; padding: 12px; color: var(--gold); background: #111827; border: 2px solid var(--border-accent); border-radius: var(--radius-md); outline: none; }
        .otp-large-input:focus { border-color: var(--gold); box-shadow: 0 0 15px rgba(255, 215, 0, 0.3); }
        .btn-primary { width: 100%; padding: 13px; background: linear-gradient(135deg, #FF2A85, #FF5E3A); color: white; border: none; border-radius: var(--radius-sm); font-size: 14px; font-weight: 700; cursor: pointer; box-shadow: 0 4px 15px rgba(255, 42, 133, 0.35); transition: all 0.2s ease; }
        .btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(255, 42, 133, 0.5); }
        .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
        .btn-secondary { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 9px 16px; background: var(--bg-card); color: var(--text-primary); border: 1px solid var(--border-color); border-radius: var(--radius-sm); font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
        .btn-secondary:hover { background: var(--bg-card-hover); border-color: var(--primary); }
        .btn-success { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        .btn-danger { background: rgba(255, 23, 68, 0.15); color: var(--danger); border: 1px solid rgba(255, 23, 68, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        #adminApp { display: none; min-height: 100vh; }
        .sidebar { width: 260px; background: #0B0F19; border-right: 1px solid var(--border-color); display: flex; flex-direction: column; position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; transition: transform 0.3s ease; }
        .sidebar-brand { padding: 20px 18px; display: flex; align-items: center; gap: 12px; border-bottom: 1px solid var(--border-color); }
        .sidebar-brand-logo { width: 40px; height: 40px; border-radius: 12px; background: linear-gradient(135deg, #FF2A85, #FFD700); display: flex; align-items: center; justify-content: center; font-size: 20px; }
        .sidebar-menu { flex: 1; padding: 16px 12px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; }
        .menu-category { font-size: 10.5px; font-weight: 800; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.8px; padding: 10px 10px 4px; }
        .menu-item { display: flex; align-items: center; gap: 12px; padding: 11px 14px; border-radius: var(--radius-sm); color: var(--text-secondary); font-size: 13.5px; font-weight: 600; cursor: pointer; transition: all 0.15s ease; text-decoration: none; }
        .menu-item:hover { background: var(--bg-card); color: var(--text-primary); }
        .menu-item.active { background: rgba(255, 42, 133, 0.18); color: #FFFFFF; border: 1px solid var(--border-accent); font-weight: 700; }
        .menu-item-icon { font-size: 18px; width: 22px; text-align: center; }
        .sidebar-footer { padding: 14px 16px; border-top: 1px solid var(--border-color); background: rgba(0,0,0,0.25); }
        .main-wrapper { flex: 1; margin-left: 260px; min-height: 100vh; display: flex; flex-direction: column; background: var(--bg-base); }
        .topbar { height: 68px; background: rgba(11, 15, 25, 0.85); backdrop-filter: blur(12px); border-bottom: 1px solid var(--border-color); padding: 0 24px; display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 90; }
        .mobile-menu-btn { display: none; background: none; border: none; color: white; font-size: 24px; cursor: pointer; }
        .topbar-title-wrap h2 { font-size: 18px; font-weight: 800; color: #FFFFFF; }
        .topbar-title-wrap p { font-size: 12px; color: var(--text-muted); }
        .topbar-actions { display: flex; align-items: center; gap: 12px; }
        .badge-role { padding: 4px 10px; border-radius: 999px; font-size: 10.5px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }
        .role-super-admin { background: rgba(255, 215, 0, 0.15); color: var(--gold); border: 1px solid rgba(255, 215, 0, 0.4); }
        .role-active { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.4); }
        .role-in-progress { background: rgba(0, 229, 255, 0.15); color: var(--cyan); border: 1px solid rgba(0, 229, 255, 0.4); }
        .role-pending { background: rgba(255, 145, 0, 0.15); color: var(--warning); border: 1px solid rgba(255, 145, 0, 0.4); }
        .content-area { padding: 24px; flex: 1; }
        .tab-content { display: none; }
        .tab-content.active { display: block; animation: fadeIn 0.25s ease; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .stat-card { padding: 18px; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); position: relative; overflow: hidden; }
        .stat-card-title { font-size: 12px; font-weight: 600; color: var(--text-muted); margin-bottom: 6px; }
        .stat-card-value { font-size: 26px; font-weight: 800; color: #FFFFFF; }
        .stat-card-icon { position: absolute; right: 16px; top: 16px; font-size: 24px; opacity: 0.6; }
        .table-responsive { width: 100%; overflow-x: auto; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); margin-top: 16px; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { padding: 12px 14px; font-size: 11.5px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.6px; border-bottom: 1px solid var(--border-color); background: rgba(0,0,0,0.25); }
        td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--border-color); color: var(--text-secondary); }
        tr:hover td { background: rgba(255, 255, 255, 0.02); color: var(--text-primary); }
        .avatar-sm { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border-color); }
        .user-cell { display: flex; align-items: center; gap: 10px; font-weight: 600; color: #FFFFFF; }
        .progress-bar-bg { width: 100%; max-width: 140px; height: 6px; background: rgba(255,255,255,0.1); border-radius: 999px; overflow: hidden; margin-top: 4px; }
        .progress-bar-fill { height: 100%; background: linear-gradient(90deg, var(--primary), var(--gold)); border-radius: 999px; }
        .modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.8); backdrop-filter: blur(6px); display: none; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
        .modal-box { width: 100%; max-width: 540px; background: var(--bg-surface); border: 1px solid var(--border-accent); border-radius: var(--radius-lg); padding: 28px; box-shadow: 0 25px 60px rgba(0,0,0,0.85); max-height: 90vh; overflow-y: auto; }
        .modal-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; border-bottom: 1px solid var(--border-color); padding-bottom: 12px; }
        .close-btn { background: none; border: none; color: var(--text-muted); font-size: 22px; cursor: pointer; }
        .toast-container { position: fixed; bottom: 24px; right: 24px; z-index: 2000; display: flex; flex-direction: column; gap: 10px; }
        .toast { padding: 12px 20px; border-radius: var(--radius-sm); font-size: 13px; font-weight: 600; box-shadow: 0 10px 30px rgba(0,0,0,0.5); display: flex; align-items: center; gap: 8px; animation: slideIn 0.3s ease; }
        .toast-success { background: #00E676; color: #003314; }
        .toast-error { background: #FF1744; color: white; }
        .toast-info { background: #00E5FF; color: #002233; }
        @keyframes slideIn { from { transform: translateX(100%); } to { transform: translateX(0); } }
        @media (max-width: 900px) { .sidebar { transform: translateX(-100%); } .sidebar.open { transform: translateX(0); } .main-wrapper { margin-left: 0; } .mobile-menu-btn { display: block; } }
    </style>
</head>
<body>
"""

    private const val HTML_BODY_AUTH = """
    <!-- 1. Step 1: Blank Login Form (Auto-fill removed, user enters own info) -->
    <div id="loginScreen" class="auth-screen">
        <div class="auth-box glass-panel">
            <div class="brand-badge">🛡️ OFFICIAL 1 • SECURE GATEWAY</div>
            <h1 style="font-size: 22px; font-weight: 800; margin-bottom: 4px; color: #FFFFFF;">Admin Verification</h1>
            <p style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 22px;">Please enter your authorized administrator credentials</p>
            <div id="loginAlertBox" style="display: none; padding: 10px 14px; background: rgba(255,23,68,0.15); border: 1px solid rgba(255,23,68,0.35); border-radius: var(--radius-sm); color: #FF6B8B; font-size: 12.5px; margin-bottom: 16px; text-align: left;"></div>
            
            <form id="loginForm" onsubmit="handleLoginSubmit(event)" autocomplete="off">
                <div class="form-group">
                    <label class="form-label">User Name</label>
                    <input type="text" id="loginUserName" class="form-input" placeholder="Enter User Name" value="" required autocomplete="off">
                </div>
                <div class="form-group">
                    <label class="form-label">Admin ID</label>
                    <input type="text" id="loginAdminId" class="form-input" placeholder="Enter Admin ID" value="" required autocomplete="off">
                </div>
                <div class="form-group">
                    <label class="form-label">Password</label>
                    <input type="password" id="loginPassword" class="form-input" placeholder="Enter Password" value="" required autocomplete="off">
                </div>
                <div class="form-group">
                    <label class="form-label">Mobile Number</label>
                    <input type="text" id="loginMobileNumber" class="form-input" placeholder="+92 3XX XXXXXXX" value="" required autocomplete="off">
                </div>
                <button type="submit" id="loginBtn" class="btn-primary" style="margin-top: 8px;"><span>🔐 Verify & Send WhatsApp Code</span></button>
            </form>
            <div style="margin-top: 20px; font-size: 11px; color: var(--text-muted);">
                Protected by SHA-256 & Authorized WhatsApp 2FA OTP
            </div>
        </div>
    </div>

    <!-- 2. Step 2: WhatsApp OTP Verification Screen -->
    <div id="verifyScreen" class="auth-screen" style="display: none;">
        <div class="auth-box glass-panel">
            <div class="brand-badge" style="background: rgba(0, 230, 118, 0.12); border-color: rgba(0, 230, 118, 0.35); color: var(--emerald);">📲 WhatsApp Verification</div>
            <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 4px; color: #FFFFFF;">Enter OTP Code</h2>
            <p style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 16px;">Two-factor authentication code sent via authorized WhatsApp service</p>
            <div id="verifyAlertBox" style="display: none; padding: 10px 14px; border-radius: var(--radius-sm); font-size: 12.5px; margin-bottom: 16px; text-align: left;"></div>
            
            <div class="form-group" style="text-align: center;">
                <label class="form-label">Mobile Number</label>
                <div style="font-family: 'JetBrains Mono', monospace; font-size: 15px; font-weight: 700; color: var(--cyan);" id="verifyMaskedMobileDisplay">+92 3XX *** **XX</div>
            </div>

            <div id="otpInputSection" style="margin-top: 14px;">
                <label class="form-label" style="text-align: center;">Verification Code</label>
                <div class="otp-input-wrap">
                    <input type="text" id="verifyOtpCode" class="otp-large-input" maxlength="6" placeholder="______" autocomplete="off">
                </div>
                <div id="cooldownTimerWrap" style="font-size: 12px; color: var(--warning); margin-bottom: 12px;">
                    ⏳ Resend code available in: <strong id="cooldownTimerText">60s</strong>
                </div>
                <div style="display: flex; gap: 8px; margin-bottom: 8px;">
                    <button type="button" id="resendCodeBtn" onclick="handleResendWhatsAppOtp()" class="btn-secondary" style="flex: 1; font-weight: 700;" disabled>
                        <span>🔄 Resend Code</span>
                    </button>
                    <button type="button" id="verifyOtpBtn" onclick="handleVerifyWhatsAppOtp()" class="btn-primary" style="flex: 2;">
                        <span>✅ Verify & Open Panel</span>
                    </button>
                </div>
            </div>
            <div style="margin-top: 18px;">
                <a href="#" onclick="showLoginView(); return false;" style="font-size: 12px; color: var(--text-muted); text-decoration: none;">← Back to Credentials</a>
            </div>
        </div>
    </div>
"""

    private const val HTML_BODY_APP = """
    <!-- 3. Official 1 Admin Panel (Dashboard & Link Section) -->
    <div id="adminApp">
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-logo">🛡️</div>
                <div>
                    <h3 id="sidebarPanelTitle" style="font-size: 15px; font-weight: 800; color: #FFFFFF;">OFFICIAL 1</h3>
                    <p style="font-size: 11px; color: var(--gold); font-weight: 700;">ADMIN PANEL</p>
                </div>
            </div>
            <div class="sidebar-menu">
                <div class="menu-category">Main Menu</div>
                <a class="menu-item active" onclick="switchTab('dashboard')">
                    <span class="menu-item-icon">📊</span> Dashboard
                </a>
                <a class="menu-item" onclick="switchTab('link')">
                    <span class="menu-item-icon">🔗</span> <strong>LINK</strong>
                </a>
                <div class="menu-category">Session</div>
                <a class="menu-item" onclick="handleLogout()" style="color: var(--danger);">
                    <span class="menu-item-icon">🚪</span> Logout
                </a>
            </div>
            <div class="sidebar-footer">
                <div style="display: flex; align-items: center; gap: 10px;">
                    <div style="width: 36px; height: 36px; border-radius: 50%; background: linear-gradient(135deg, #FF2A85, #FFD700); display: flex; align-items: center; justify-content: center; font-weight: 800; color: white;">👑</div>
                    <div style="flex: 1; overflow: hidden;">
                        <div id="sidebarAdminName" style="font-size: 13px; font-weight: 700; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #FFF;">Sherry</div>
                        <div id="sidebarAdminId" style="font-family: 'JetBrains Mono', monospace; font-size: 10.5px; color: var(--gold); margin-top: 2px;">565656565666555</div>
                    </div>
                </div>
            </div>
        </aside>

        <main class="main-wrapper">
            <header class="topbar">
                <div style="display: flex; align-items: center; gap: 14px;">
                    <button class="mobile-menu-btn" onclick="toggleSidebar()">☰</button>
                    <div class="topbar-title-wrap">
                        <h2 id="currentTabTitle">Dashboard</h2>
                        <p id="currentTabSubtitle">Real-time overview of users registered under your link</p>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button class="btn-secondary" onclick="copyAdminLink()" style="border-color: var(--gold); color: var(--gold); font-weight: 700;">📋 Copy My Link</button>
                    <div id="topbarStatusBadge" class="badge-role role-super-admin">2FA VERIFIED 🛡️</div>
                    <button class="btn-secondary" onclick="refreshCurrentTab()" title="Refresh Data">🔄 Refresh</button>
                </div>
            </header>

            <div class="content-area">
                <!-- 1. DASHBOARD -->
                <section id="tab-dashboard" class="tab-content active">
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-card-title">My Registered Users</div>
                            <div class="stat-card-value" id="statTotalUsers" style="color: var(--cyan);">0</div>
                            <div class="stat-card-icon">👥</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-card-title">Active Working Users</div>
                            <div class="stat-card-value" id="statActiveUsers" style="color: var(--emerald);">0</div>
                            <div class="stat-card-icon">🟢</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-card-title">Completed Tasks / Goals</div>
                            <div class="stat-card-value" id="statCompletedWork" style="color: var(--gold);">0</div>
                            <div class="stat-card-icon">✅</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-card-title">In-Progress Tasks</div>
                            <div class="stat-card-value" id="statInProgressWork" style="color: var(--primary);">0</div>
                            <div class="stat-card-icon">⏳</div>
                        </div>
                    </div>

                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 20px;">
                        <div class="glass-card" style="padding: 20px;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
                                <h3 style="font-size: 15px; font-weight: 700; color: #FFFFFF;">🔗 Users Under My Link (Quick View)</h3>
                                <button class="btn-secondary" onclick="switchTab('link')" style="font-size: 12px; padding: 5px 10px;">View Full Link Section →</button>
                            </div>
                            <div class="table-responsive" style="margin-top: 0;">
                                <table>
                                    <thead><tr><th>User</th><th>Assigned Work</th><th>Status</th><th>Work Progress</th></tr></thead>
                                    <tbody id="dashboardUsersTableBody"><tr><td colspan="4" style="text-align: center; color: var(--text-muted);">Loading my link users...</td></tr></tbody>
                                </table>
                            </div>
                        </div>

                        <div class="glass-card" style="padding: 20px; display: flex; flex-direction: column; gap: 16px;">
                            <h3 style="font-size: 15px; font-weight: 700; color: var(--gold);">⚡ Official 1 Link Tools</h3>
                            <div style="background: rgba(0,0,0,0.3); border: 1px solid var(--border-color); border-radius: var(--radius-sm); padding: 12px;">
                                <div style="font-size: 11px; font-weight: 700; color: var(--text-muted); margin-bottom: 6px; text-transform: uppercase;">My Registration Link</div>
                                <div id="myLinkDisplay" style="font-family: 'JetBrains Mono', monospace; font-size: 12px; color: var(--cyan); word-break: break-all; margin-bottom: 10px;">https://official1.live/link?admin=...</div>
                                <button class="btn-primary" onclick="copyAdminLink()" style="padding: 8px 12px; font-size: 12px;">📋 Copy Link to Clipboard</button>
                            </div>
                            <div style="display: flex; flex-direction: column; gap: 8px;">
                                <button class="btn-secondary" onclick="openAddLinkUserModal()" style="justify-content: flex-start; font-weight: 700;">➕ Register New User Under Link</button>
                                <button class="btn-secondary" onclick="switchTab('link')" style="justify-content: flex-start; font-weight: 700;">🔗 Open Full Link Management</button>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- 2. LINK SECTION -->
                <section id="tab-link" class="tab-content">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; gap: 12px; flex-wrap: wrap;">
                        <div style="display: flex; gap: 10px; flex: 1; min-width: 280px;">
                            <input type="text" id="linkSearchInput" class="form-input" style="max-width: 340px;" placeholder="🔍 Search by name, user ID or assigned work..." oninput="debounceLinkSearch()">
                            <select id="linkStatusFilter" class="form-input" style="width: 170px;" onchange="loadLinkUsers()">
                                <option value="">All Statuses</option>
                                <option value="Active">Active</option>
                                <option value="In Progress">In Progress</option>
                                <option value="Completed">Completed</option>
                                <option value="Active Live">Active Live</option>
                                <option value="Pending Review">Pending Review</option>
                            </select>
                        </div>
                        <div style="display: flex; gap: 10px;">
                            <button class="btn-secondary" onclick="copyAdminLink()" style="font-weight: 700;">📋 Copy Link</button>
                            <button class="btn-primary" onclick="openAddLinkUserModal()" style="width: auto; padding: 10px 18px;">➕ Add User to Link</button>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>User ID</th>
                                    <th>Assigned Work</th>
                                    <th>Work Status</th>
                                    <th>Performance / Activity</th>
                                    <th>Last Active</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="linkUsersTableBody">
                                <tr><td colspan="7" style="text-align: center;">Loading users under your link...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </main>
    </div>
"""

    private const val HTML_MODALS = """
    <!-- Modal: Add User Under Link -->
    <div id="addLinkUserModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3 style="font-size: 16px; font-weight: 800; color: #FFF;">➕ Register User Under My Link</h3>
                <button class="close-btn" onclick="closeModal('addLinkUserModal')">×</button>
            </div>
            <form onsubmit="handleAddLinkUserSubmit(event)" autocomplete="off">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">User ID *</label>
                        <input type="text" id="addUserId" class="form-input" placeholder="e.g. usr_98124" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">User Name *</label>
                        <input type="text" id="addUserName" class="form-input" placeholder="e.g. Bilal Ahmed" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Assigned Work *</label>
                    <input type="text" id="addAssignedWork" class="form-input" placeholder="e.g. Prime-Time Audio Host (Target 40h/month)" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Work Status</label>
                        <select id="addWorkStatus" class="form-input">
                            <option value="In Progress">In Progress</option>
                            <option value="Active Live">Active Live</option>
                            <option value="Completed">Completed</option>
                            <option value="Pending Review">Pending Review</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Work Category</label>
                        <select id="addWorkCategory" class="form-input">
                            <option value="Voice Hosting">Voice Hosting</option>
                            <option value="Moderation">Moderation</option>
                            <option value="Agency BD">Agency BD</option>
                            <option value="VIP Support">VIP Support</option>
                        </select>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Target Streaming Hours</label>
                        <input type="number" id="addTargetHours" class="form-input" value="40" step="0.5">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Target Diamonds</label>
                        <input type="number" id="addTargetDiamonds" class="form-input" value="50000">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Performance / Activity Info</label>
                    <input type="text" id="addActivityInfo" class="form-input" placeholder="e.g. Live 12h • 15k Diamonds • Punctual">
                </div>

                <div class="form-group">
                    <label class="form-label">Notes</label>
                    <input type="text" id="addNotes" class="form-input" placeholder="Admin notes on this user">
                </div>

                <button type="submit" class="btn-primary" style="margin-top: 8px;">💾 Save & Register Under Link</button>
            </form>
        </div>
    </div>

    <!-- Modal: Edit User Work & Status -->
    <div id="editLinkUserModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3 style="font-size: 16px; font-weight: 800; color: #FFF;">✏️ Edit User Work & Status</h3>
                <button class="close-btn" onclick="closeModal('editLinkUserModal')">×</button>
            </div>
            <form onsubmit="handleUpdateLinkUserSubmit(event)" autocomplete="off">
                <input type="hidden" id="editRecordId">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px; padding: 10px; background: rgba(255,255,255,0.03); border-radius: var(--radius-sm);">
                    <div><span style="font-size: 11px; color: var(--text-muted);">USER NAME</span><div id="editUserNameDisplay" style="font-weight: 700; color: #FFF;">-</div></div>
                    <div><span style="font-size: 11px; color: var(--text-muted);">USER ID</span><div id="editUserIdDisplay" style="font-family: monospace; color: var(--cyan);">-</div></div>
                </div>

                <div class="form-group">
                    <label class="form-label">Assigned Work *</label>
                    <input type="text" id="editAssignedWork" class="form-input" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Work Status</label>
                        <select id="editWorkStatus" class="form-input">
                            <option value="In Progress">In Progress</option>
                            <option value="Active Live">Active Live</option>
                            <option value="Completed">Completed</option>
                            <option value="Pending Review">Pending Review</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Account Status</label>
                        <select id="editUserStatus" class="form-input">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                            <option value="On Leave">On Leave</option>
                        </select>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label class="form-label">Completed / Target Hours</label>
                        <div style="display: flex; gap: 6px;">
                            <input type="number" id="editCompletedHours" class="form-input" placeholder="Completed" step="0.5">
                            <input type="number" id="editTargetHours" class="form-input" placeholder="Target" step="0.5">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Earned / Target Diamonds</label>
                        <div style="display: flex; gap: 6px;">
                            <input type="number" id="editEarnedDiamonds" class="form-input" placeholder="Earned">
                            <input type="number" id="editTargetDiamonds" class="form-input" placeholder="Target">
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Activity Summary</label>
                    <input type="text" id="editActivityInfo" class="form-input">
                </div>

                <div class="form-group">
                    <label class="form-label">Notes</label>
                    <input type="text" id="editNotes" class="form-input">
                </div>

                <button type="submit" class="btn-primary" style="margin-top: 8px;">💾 Update Work Assignment</button>
            </form>
        </div>
    </div>

    <!-- Toast Notifications -->
    <div id="toastContainer" class="toast-container"></div>
"""

    private const val JS_AUTH = """
    <script>
        let currentAdminSession = null;
        let currentPreAuthToken = '';
        let currentPreAuthMobile = '';
        let cooldownInterval = null;

        window.addEventListener('DOMContentLoaded', () => {
            // Ensure inputs are clean on page load
            const userNameInput = document.getElementById('loginUserName');
            const adminIdInput = document.getElementById('loginAdminId');
            const passwordInput = document.getElementById('loginPassword');
            const mobileInput = document.getElementById('loginMobileNumber');
            if (userNameInput) userNameInput.value = '';
            if (adminIdInput) adminIdInput.value = '';
            if (passwordInput) passwordInput.value = '';
            if (mobileInput) mobileInput.value = '';

            const savedToken = sessionStorage.getItem('adminToken');
            if (savedToken) {
                verifyExistingSession(savedToken);
            } else {
                showLoginView();
            }
        });

        function showToast(msg, type = 'info') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.innerHTML = (type === 'success' ? '✅ ' : (type === 'error' ? '❌ ' : 'ℹ️ ')) + msg;
            container.appendChild(toast);
            setTimeout(() => { toast.remove(); }, 3500);
        }

        function showLoginView() {
            document.getElementById('loginScreen').style.display = 'flex';
            document.getElementById('verifyScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'none';
        }

        function showVerifyView(maskedMobile) {
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'flex';
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('verifyMaskedMobileDisplay').innerText = maskedMobile || currentPreAuthMobile || '+92 3XX *** **XX';
            const codeInput = document.getElementById('verifyOtpCode');
            if (codeInput) {
                codeInput.value = '';
                codeInput.focus();
            }
            startCooldownTimer(60);
        }

        function showAdminDashboard() {
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'flex';
            switchTab('dashboard');
        }

        async function verifyExistingSession(token) {
            try {
                const res = await fetch('/api/admin/dashboard', {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (res.ok) {
                    const data = await res.json();
                    currentAdminSession = {
                        token: token,
                        userId: data.adminId,
                        username: data.adminName,
                        panelName: data.panelName || 'Official 1',
                        role: data.adminRole || 'Super Admin',
                        mobileNumber: data.mobileNumber
                    };
                    document.getElementById('sidebarPanelTitle').innerText = (data.panelName || 'OFFICIAL 1').toUpperCase();
                    document.getElementById('sidebarAdminName').innerText = data.adminName;
                    document.getElementById('sidebarAdminId').innerText = data.adminId;
                    showAdminDashboard();
                } else {
                    sessionStorage.removeItem('adminToken');
                    showLoginView();
                }
            } catch (e) {
                showLoginView();
            }
        }

        async function handleLoginSubmit(e) {
            e.preventDefault();
            const userName = document.getElementById('loginUserName').value.trim();
            const adminId = document.getElementById('loginAdminId').value.trim();
            const password = document.getElementById('loginPassword').value.trim();
            const mobileNumber = document.getElementById('loginMobileNumber').value.trim();
            const alertBox = document.getElementById('loginAlertBox');
            const btn = document.getElementById('loginBtn');

            if (!userName || !adminId || !password || !mobileNumber) {
                alertBox.innerText = 'Please enter User Name, Admin ID, Password, and Mobile Number.';
                alertBox.style.display = 'block';
                return;
            }

            alertBox.style.display = 'none';
            btn.disabled = true;
            btn.innerHTML = '<span>⏳ Verifying Credentials...</span>';

            try {
                const res = await fetch('/api/admin/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userName, adminId, password, mobileNumber })
                });
                const data = await res.json();
                if (data.success && data.step === 'OTP_REQUIRED') {
                    currentPreAuthToken = data.preAuthToken;
                    currentPreAuthMobile = data.mobileNumber;
                    showToast('Information verified! WhatsApp OTP code dispatched.', 'success');
                    showVerifyView(data.mobileMasked);
                } else {
                    alertBox.innerText = data.message || 'Invalid Admin Information. Access Denied.';
                    alertBox.style.display = 'block';
                    showToast(data.message || 'Login failed', 'error');
                }
            } catch (err) {
                alertBox.innerText = 'Network error connecting to Admin Server.';
                alertBox.style.display = 'block';
                showToast('Connection failed', 'error');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>🔐 Verify & Send WhatsApp Code</span>';
            }
        }

        async function handleResendWhatsAppOtp() {
            if (!currentPreAuthToken) {
                showToast('Session expired. Please login again.', 'error');
                showLoginView();
                return;
            }
            const resendBtn = document.getElementById('resendCodeBtn');
            resendBtn.disabled = true;

            try {
                const res = await fetch('/api/admin/send-whatsapp-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ preAuthToken: currentPreAuthToken, mobileNumber: currentPreAuthMobile })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('WhatsApp OTP resent successfully!', 'success');
                    startCooldownTimer(data.cooldownSeconds || 60);
                } else {
                    const errMsg = data.message || 'Unable to send verification code. Please check the number or try again later.';
                    showToast(errMsg, 'error');
                    resendBtn.disabled = false;
                }
            } catch (err) {
                showToast('Network error requesting code', 'error');
                resendBtn.disabled = false;
            }
        }

        function startCooldownTimer(seconds) {
            clearInterval(cooldownInterval);
            const wrap = document.getElementById('cooldownTimerWrap');
            const text = document.getElementById('cooldownTimerText');
            const resendBtn = document.getElementById('resendCodeBtn');
            wrap.style.display = 'block';
            resendBtn.disabled = true;
            let remaining = seconds;
            text.innerText = remaining + 's';

            cooldownInterval = setInterval(() => {
                remaining--;
                if (remaining <= 0) {
                    clearInterval(cooldownInterval);
                    wrap.style.display = 'none';
                    resendBtn.disabled = false;
                } else {
                    text.innerText = remaining + 's';
                }
            }, 1000);
        }

        async function handleVerifyWhatsAppOtp() {
            if (!currentPreAuthToken) {
                showToast('Verification session expired. Please login again.', 'error');
                showLoginView();
                return;
            }
            const code = document.getElementById('verifyOtpCode').value.trim();
            const alertBox = document.getElementById('verifyAlertBox');
            const verifyBtn = document.getElementById('verifyOtpBtn');

            if (!code || code.length !== 6) {
                showToast('Please enter the 6-digit WhatsApp verification code.', 'warning');
                return;
            }
            verifyBtn.disabled = true;
            verifyBtn.innerHTML = '<span>⏳ Verifying Code...</span>';

            try {
                const res = await fetch('/api/admin/verify-whatsapp-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ preAuthToken: currentPreAuthToken, code })
                });
                const data = await res.json();
                if (data.success && data.session) {
                    currentAdminSession = data.session;
                    sessionStorage.setItem('adminToken', currentAdminSession.token);
                    document.getElementById('sidebarPanelTitle').innerText = (currentAdminSession.panelName || 'OFFICIAL 1').toUpperCase();
                    document.getElementById('sidebarAdminName').innerText = currentAdminSession.username;
                    document.getElementById('sidebarAdminId').innerText = currentAdminSession.userId;
                    showToast('WhatsApp 2FA Verified! Official 1 Panel Open.', 'success');
                    showAdminDashboard();
                } else {
                    alertBox.style.display = 'block';
                    alertBox.style.background = 'rgba(255, 23, 68, 0.15)';
                    alertBox.style.borderColor = 'rgba(255, 23, 68, 0.35)';
                    alertBox.style.color = '#FF6B8B';
                    alertBox.innerText = '❌ ' + (data.message || 'Incorrect verification code.');
                    showToast(data.message || 'Verification failed', 'error');
                }
            } catch (err) {
                showToast('Network error during verification', 'error');
            } finally {
                verifyBtn.disabled = false;
                verifyBtn.innerHTML = '<span>✅ Verify & Open Panel</span>';
            }
        }

        function getAuthHeaders() {
            return {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + (currentAdminSession ? currentAdminSession.token : '')
            };
        }

        async function handleLogout() {
            if (currentAdminSession) {
                try {
                    await fetch('/api/admin/logout', { method: 'POST', headers: getAuthHeaders() });
                } catch (e) {}
            }
            currentAdminSession = null;
            currentPreAuthToken = '';
            sessionStorage.removeItem('adminToken');
            showToast('Logged out securely', 'info');
            showLoginView();
        }
    """

    private const val JS_APP = """
        let allLinkUsers = [];

        function switchTab(tabId) {
            document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));

            const targetContent = document.getElementById('tab-' + tabId);
            if (targetContent) targetContent.classList.add('active');

            const menuEl = Array.from(document.querySelectorAll('.menu-item')).find(el => el.getAttribute('onclick')?.includes(tabId));
            if (menuEl) menuEl.classList.add('active');

            const titleMap = {
                dashboard: ['Dashboard', 'Real-time overview of users registered under your link'],
                link: ['🔗 LINK Management', 'Users registered under your unique admin link & their assigned work']
            };

            const info = titleMap[tabId] || ['Official 1', 'Admin Control Panel'];
            document.getElementById('currentTabTitle').innerText = info[0];
            document.getElementById('currentTabSubtitle').innerText = info[1];
            loadTabData(tabId);
        }

        function refreshCurrentTab() {
            const activeTab = document.querySelector('.tab-content.active')?.id?.replace('tab-', '') || 'dashboard';
            loadTabData(activeTab);
            showToast('Refreshed ' + activeTab, 'info');
        }

        function loadTabData(tabId) {
            if (!currentAdminSession) return;
            if (tabId === 'dashboard') loadDashboardStats();
            if (tabId === 'link') loadLinkUsers();
        }

        async function loadDashboardStats() {
            try {
                const res = await fetch('/api/admin/dashboard', { headers: getAuthHeaders() });
                const data = await res.json();
                document.getElementById('statTotalUsers').innerText = data.totalLinkUsers || 0;
                document.getElementById('statActiveUsers').innerText = data.activeLinkUsers || 0;
                document.getElementById('statCompletedWork').innerText = data.completedWork || 0;
                document.getElementById('statInProgressWork').innerText = data.inProgressWork || 0;

                const linkUrl = data.adminLinkUrl || ('https://official1.live/link?admin=' + (data.adminId || ''));
                document.getElementById('myLinkDisplay').innerText = linkUrl;

                // Load users for the quick preview
                loadLinkUsers(true);
            } catch (e) { console.error(e); }
        }

        let linkSearchTimeout = null;
        function debounceLinkSearch() {
            clearTimeout(linkSearchTimeout);
            linkSearchTimeout = setTimeout(loadLinkUsers, 300);
        }

        async function loadLinkUsers(isDashboardQuickView = false) {
            const query = document.getElementById('linkSearchInput')?.value || '';
            const statusFilter = document.getElementById('linkStatusFilter')?.value || '';
            try {
                const res = await fetch('/api/admin/link/users?query=' + encodeURIComponent(query), { headers: getAuthHeaders() });
                const users = await res.json();
                allLinkUsers = users;

                let filtered = users;
                if (statusFilter) {
                    filtered = users.filter(u => u.status === statusFilter || u.workStatus === statusFilter);
                }

                // Render Dashboard Quick Table
                const dashTbody = document.getElementById('dashboardUsersTableBody');
                if (dashTbody) {
                    dashTbody.innerHTML = '';
                    if (users.length === 0) {
                        dashTbody.innerHTML = '<tr><td colspan="4" style="text-align: center; color: var(--text-muted);">No users registered under your link yet.</td></tr>';
                    } else {
                        users.slice(0, 5).forEach(u => {
                            const row = document.createElement('tr');
                            const statusClass = u.workStatus === 'Completed' ? 'role-active' : (u.workStatus === 'Active Live' ? 'role-active' : 'role-in-progress');
                            const pct = u.targetHours > 0 ? Math.min(100, Math.round((u.completedHours / u.targetHours) * 100)) : 0;
                            row.innerHTML = `
                                <td><div class="user-cell"><img src="${'$'}{u.userAvatar}" class="avatar-sm"><div><div>${'$'}{u.userName}</div><div style="font-size: 11px; font-family: monospace; color: var(--cyan);">${'$'}{u.userId}</div></div></div></td>
                                <td><strong style="color: #FFF;">${'$'}{u.assignedWork}</strong><div style="font-size: 11px; color: var(--text-muted);">${'$'}{u.workCategory}</div></td>
                                <td><span class="badge-role ${'$'}{statusClass}">${'$'}{u.workStatus}</span></td>
                                <td>
                                    <div style="font-size: 11.5px; color: #FFF;">${'$'}{u.completedHours}h / ${'$'}{u.targetHours}h (${'$'}{pct}%)</div>
                                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${'$'}{pct}%;"></div></div>
                                </td>
                            `;
                            dashTbody.appendChild(row);
                        });
                    }
                }

                // Render Main Link Table
                const linkTbody = document.getElementById('linkUsersTableBody');
                if (linkTbody && !isDashboardQuickView) {
                    linkTbody.innerHTML = '';
                    if (filtered.length === 0) {
                        linkTbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 24px; color: var(--text-muted);">No users found matching query. Click "+ Add User to Link" to register a user.</td></tr>';
                        return;
                    }

                    filtered.forEach(u => {
                        const row = document.createElement('tr');
                        const statusClass = u.workStatus === 'Completed' ? 'role-active' : (u.workStatus === 'Active Live' ? 'role-active' : 'role-in-progress');
                        const pct = u.targetHours > 0 ? Math.min(100, Math.round((u.completedHours / u.targetHours) * 100)) : 0;
                        row.innerHTML = `
                            <td>
                                <div class="user-cell">
                                    <img src="${'$'}{u.userAvatar}" class="avatar-sm">
                                    <div>
                                        <div style="font-weight: 700; color: #FFFFFF;">${'$'}{u.userName}</div>
                                        <div style="font-size: 11px; color: var(--text-muted);">Joined ${'$'}{u.joinedDate}</div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span style="font-family: 'JetBrains Mono', monospace; font-size: 12px; color: var(--cyan); font-weight: 700;">${'$'}{u.userId}</span>
                            </td>
                            <td>
                                <div style="font-weight: 700; color: #FFF;">${'$'}{u.assignedWork}</div>
                                <span style="font-size: 10.5px; padding: 2px 6px; background: rgba(255,255,255,0.06); border-radius: 4px; color: var(--text-secondary);">${'$'}{u.workCategory}</span>
                            </td>
                            <td>
                                <span class="badge-role ${'$'}{statusClass}">${'$'}{u.workStatus}</span>
                            </td>
                            <td>
                                <div style="font-size: 12px; color: #FFF; font-weight: 600;">${'$'}{u.activityInfo}</div>
                                <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${'$'}{pct}%;"></div></div>
                                <div style="font-size: 10.5px; color: var(--text-muted); margin-top: 2px;">${'$'}{u.completedHours}h streamed • 💎 ${'$'}{u.earnedDiamonds?.toLocaleString()} earned</div>
                            </td>
                            <td>
                                <span style="font-size: 12px; color: ${'$'}{u.lastActive.includes('Online') ? 'var(--emerald)' : 'var(--text-secondary)'}; font-weight: 600;">${'$'}{u.lastActive}</span>
                            </td>
                            <td>
                                <div style="display: flex; gap: 6px;">
                                    <button class="btn-secondary" onclick="openEditLinkUserModal('${'$'}{u.id}')" style="padding: 5px 10px; font-size: 11.5px; font-weight: 700;">✏️ Edit</button>
                                    <button class="btn-danger" onclick="handleDeleteLinkUser('${'$'}{u.id}', '${'$'}{u.userName}')" style="padding: 5px 10px; font-size: 11.5px;">🗑️</button>
                                </div>
                            </td>
                        `;
                        linkTbody.appendChild(row);
                    });
                }
            } catch (e) { console.error(e); }
        }

        function copyAdminLink() {
            const adminId = currentAdminSession?.userId || '565656565666555';
            const url = 'https://official1.live/link?admin=' + adminId;
            if (navigator.clipboard) {
                navigator.clipboard.writeText(url).then(() => {
                    showToast('Official 1 Admin Link copied: ' + url, 'success');
                }).catch(() => {
                    prompt('Copy your link:', url);
                });
            } else {
                prompt('Copy your link:', url);
            }
        }

        function openModal(id) {
            const m = document.getElementById(id);
            if (m) m.style.display = 'flex';
        }

        function closeModal(id) {
            const m = document.getElementById(id);
            if (m) m.style.display = 'none';
        }

        function openAddLinkUserModal() {
            document.getElementById('addUserId').value = '';
            document.getElementById('addUserName').value = '';
            document.getElementById('addAssignedWork').value = '';
            document.getElementById('addActivityInfo').value = '';
            document.getElementById('addNotes').value = '';
            openModal('addLinkUserModal');
        }

        async function handleAddLinkUserSubmit(e) {
            e.preventDefault();
            const userId = document.getElementById('addUserId').value.trim();
            const userName = document.getElementById('addUserName').value.trim();
            const assignedWork = document.getElementById('addAssignedWork').value.trim();
            const workStatus = document.getElementById('addWorkStatus').value;
            const workCategory = document.getElementById('addWorkCategory').value;
            const targetHours = parseFloat(document.getElementById('addTargetHours').value) || 40.0;
            const targetDiamonds = parseInt(document.getElementById('addTargetDiamonds').value) || 50000;
            const activityInfo = document.getElementById('addActivityInfo').value.trim();
            const notes = document.getElementById('addNotes').value.trim();

            try {
                const res = await fetch('/api/admin/link/users', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        userId, userName, assignedWork, workStatus, workCategory,
                        targetHours, targetDiamonds, activityInfo, notes, status: 'Active'
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('User registered under your link successfully!', 'success');
                    closeModal('addLinkUserModal');
                    loadLinkUsers();
                    loadDashboardStats();
                } else {
                    showToast(data.message || 'Failed to add user', 'error');
                }
            } catch (err) {
                showToast('Network error adding user', 'error');
            }
        }

        function openEditLinkUserModal(recordId) {
            const user = allLinkUsers.find(u => u.id === recordId);
            if (!user) return;

            document.getElementById('editRecordId').value = user.id;
            document.getElementById('editUserNameDisplay').innerText = user.userName;
            document.getElementById('editUserIdDisplay').innerText = user.userId;
            document.getElementById('editAssignedWork').value = user.assignedWork || '';
            document.getElementById('editWorkStatus').value = user.workStatus || 'In Progress';
            document.getElementById('editUserStatus').value = user.status || 'Active';
            document.getElementById('editCompletedHours').value = user.completedHours || 0;
            document.getElementById('editTargetHours').value = user.targetHours || 40;
            document.getElementById('editEarnedDiamonds').value = user.earnedDiamonds || 0;
            document.getElementById('editTargetDiamonds').value = user.targetDiamonds || 50000;
            document.getElementById('editActivityInfo').value = user.activityInfo || '';
            document.getElementById('editNotes').value = user.notes || '';

            openModal('editLinkUserModal');
        }

        async function handleUpdateLinkUserSubmit(e) {
            e.preventDefault();
            const id = document.getElementById('editRecordId').value;
            const assignedWork = document.getElementById('editAssignedWork').value.trim();
            const workStatus = document.getElementById('editWorkStatus').value;
            const status = document.getElementById('editUserStatus').value;
            const completedHours = parseFloat(document.getElementById('editCompletedHours').value) || 0.0;
            const targetHours = parseFloat(document.getElementById('editTargetHours').value) || 40.0;
            const earnedDiamonds = parseInt(document.getElementById('editEarnedDiamonds').value) || 0;
            const targetDiamonds = parseInt(document.getElementById('editTargetDiamonds').value) || 50000;
            const activityInfo = document.getElementById('editActivityInfo').value.trim();
            const notes = document.getElementById('editNotes').value.trim();

            try {
                const res = await fetch('/api/admin/link/users/update', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        id, assignedWork, workStatus, status,
                        completedHours, targetHours, earnedDiamonds, targetDiamonds,
                        activityInfo, notes
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Work assignment updated successfully!', 'success');
                    closeModal('editLinkUserModal');
                    loadLinkUsers();
                    loadDashboardStats();
                } else {
                    showToast(data.message || 'Failed to update work assignment', 'error');
                }
            } catch (err) {
                showToast('Network error updating user', 'error');
            }
        }

        async function handleDeleteLinkUser(id, userName) {
            if (!confirm(`Are you sure you want to remove ${'$'}{userName} from your link?`)) return;
            try {
                const res = await fetch('/api/admin/link/users/delete', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ id })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('User removed from your link', 'success');
                    loadLinkUsers();
                    loadDashboardStats();
                } else {
                    showToast(data.message || 'Failed to remove user', 'error');
                }
            } catch (err) {
                showToast('Network error removing user', 'error');
            }
        }

        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('open');
        }
    """

    private const val HTML_TAIL = """
    </script>
</body>
</html>
"""
}
