package com.example.data.admin

object AdminWebPageTemplate {

    fun getHtml(): String {
        return (HTML_HEAD + HTML_BODY_AUTH + HTML_BODY_APP + JS_AUTH + JS_APP + HTML_TAIL).replace("@@", "$")
    }

    private const val HTML_HEAD = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Official 1 — Admin Control Panel</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet">
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
        .form-group { text-align: left; margin-bottom: 16px; }
        .form-label { display: block; font-size: 12px; font-weight: 700; color: var(--text-secondary); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
        .form-input { width: 100%; padding: 12px 14px; background: #151C2C; border: 1px solid var(--border-color); border-radius: var(--radius-sm); color: #FFFFFF; font-size: 14px; outline: none; transition: all 0.2s ease; }
        .form-input:focus { border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-glow); }
        .otp-input-wrap { display: flex; justify-content: center; margin: 18px 0; }
        .otp-large-input { width: 100%; max-width: 280px; font-family: 'JetBrains Mono', monospace; font-size: 28px; font-weight: 800; letter-spacing: 12px; text-align: center; padding: 12px; color: var(--gold); background: #111827; border: 2px solid var(--border-accent); border-radius: var(--radius-md); outline: none; }
        .otp-large-input:focus { border-color: var(--gold); box-shadow: 0 0 15px rgba(255, 215, 0, 0.3); }
        .btn-primary { width: 100%; padding: 13px; background: linear-gradient(135deg, #FF2A85, #FF5E3A); color: white; border: none; border-radius: var(--radius-sm); font-size: 14px; font-weight: 700; cursor: pointer; box-shadow: 0 4px 15px rgba(255, 42, 133, 0.35); transition: all 0.2s ease; }
        .btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(255, 42, 133, 0.5); }
        .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
        .btn-secondary { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 9px 16px; background: var(--bg-card); color: var(--text-primary); border: 1px solid var(--border-color); border-radius: var(--radius-sm); font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
        .btn-secondary:hover { background: var(--bg-card-hover); border-color: var(--primary); }
        .btn-success { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        .btn-danger { background: rgba(255, 23, 68, 0.15); color: var(--danger); border: 1px solid rgba(255, 23, 68, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        #adminApp { display: none; min-height: 100vh; }
        .sidebar { width: 270px; background: #0B0F19; border-right: 1px solid var(--border-color); display: flex; flex-direction: column; position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; transition: transform 0.3s ease; }
        .sidebar-brand { padding: 20px 18px; display: flex; align-items: center; gap: 12px; border-bottom: 1px solid var(--border-color); }
        .sidebar-brand-logo { width: 40px; height: 40px; border-radius: 12px; background: linear-gradient(135deg, #FF2A85, #FFD700); display: flex; align-items: center; justify-content: center; font-size: 22px; }
        .sidebar-menu { flex: 1; padding: 12px 10px; overflow-y: auto; display: flex; flex-direction: column; gap: 2px; }
        .menu-category { font-size: 10.5px; font-weight: 800; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.8px; padding: 12px 10px 4px; }
        .menu-item { display: flex; align-items: center; gap: 11px; padding: 9px 12px; border-radius: var(--radius-sm); color: var(--text-secondary); font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s ease; text-decoration: none; }
        .menu-item:hover { background: var(--bg-card); color: var(--text-primary); }
        .menu-item.active { background: rgba(255, 42, 133, 0.15); color: #FFFFFF; border: 1px solid var(--border-accent); font-weight: 700; }
        .menu-item-icon { font-size: 16px; width: 22px; text-align: center; }
        .sidebar-footer { padding: 14px 16px; border-top: 1px solid var(--border-color); background: rgba(0,0,0,0.25); }
        .main-wrapper { flex: 1; margin-left: 270px; min-height: 100vh; display: flex; flex-direction: column; background: var(--bg-base); }
        .topbar { height: 68px; background: rgba(11, 15, 25, 0.85); backdrop-filter: blur(12px); border-bottom: 1px solid var(--border-color); padding: 0 24px; display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 90; }
        .mobile-menu-btn { display: none; background: none; border: none; color: white; font-size: 24px; cursor: pointer; }
        .topbar-title-wrap h2 { font-size: 18px; font-weight: 800; color: #FFFFFF; }
        .topbar-title-wrap p { font-size: 12px; color: var(--text-muted); }
        .topbar-actions { display: flex; align-items: center; gap: 12px; }
        .badge-role { padding: 4px 10px; border-radius: 999px; font-size: 10.5px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }
        .role-super-admin { background: rgba(255, 215, 0, 0.15); color: var(--gold); border: 1px solid rgba(255, 215, 0, 0.4); }
        .role-admin { background: rgba(255, 42, 133, 0.15); color: var(--primary); border: 1px solid rgba(255, 42, 133, 0.4); }
        .role-manager { background: rgba(0, 229, 255, 0.15); color: var(--cyan); border: 1px solid rgba(0, 229, 255, 0.4); }
        .role-bd { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.4); }
        .content-area { padding: 24px; flex: 1; }
        .tab-content { display: none; }
        .tab-content.active { display: block; animation: fadeIn 0.25s ease; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .stat-card { padding: 18px; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); position: relative; overflow: hidden; }
        .stat-card-title { font-size: 12.5px; font-weight: 600; color: var(--text-muted); margin-bottom: 6px; }
        .stat-card-value { font-size: 26px; font-weight: 800; color: #FFFFFF; }
        .stat-card-icon { position: absolute; right: 16px; top: 16px; font-size: 24px; opacity: 0.6; }
        .table-responsive { width: 100%; overflow-x: auto; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); margin-top: 16px; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { padding: 12px 14px; font-size: 11.5px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.6px; border-bottom: 1px solid var(--border-color); background: rgba(0,0,0,0.25); }
        td { padding: 12px 14px; font-size: 13px; border-bottom: 1px solid var(--border-color); color: var(--text-secondary); }
        tr:hover td { background: rgba(255, 255, 255, 0.02); color: var(--text-primary); }
        .avatar-sm { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border-color); }
        .user-cell { display: flex; align-items: center; gap: 10px; font-weight: 600; color: #FFFFFF; }
        .modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.8); backdrop-filter: blur(6px); display: none; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
        .modal-box { width: 100%; max-width: 520px; background: var(--bg-surface); border: 1px solid var(--border-accent); border-radius: var(--radius-lg); padding: 28px; box-shadow: 0 25px 60px rgba(0,0,0,0.85); max-height: 90vh; overflow-y: auto; }
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
    <!-- 1. Admin Profile First Setup Modal / Screen -->
    <div id="setupScreen" class="auth-screen" style="display: none;">
        <div class="auth-box glass-panel">
            <div class="brand-badge">⚙️ Initial Configuration</div>
            <h1 style="font-size: 22px; font-weight: 800; margin-bottom: 6px; color: #FFFFFF;">Admin Profile Setup</h1>
            <p style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 20px;">
                Enter and save your Official 1 administrative profile. Information is securely stored in database.
            </p>
            <form id="setupForm" onsubmit="handleSetupSubmit(event)">
                <div class="form-group"><label class="form-label">Panel Name</label><input type="text" id="setupPanelName" class="form-input" value="Official 1" required></div>
                <div class="form-group"><label class="form-label">Admin / User Name</label><input type="text" id="setupAdminName" class="form-input" value="Sherry" required></div>
                <div class="form-group"><label class="form-label">Admin ID</label><input type="text" id="setupAdminId" class="form-input" value="565656565666555" required></div>
                <div class="form-group"><label class="form-label">Password</label><input type="password" id="setupPassword" class="form-input" value="bismajan56b@${'$'}56" required></div>
                <div class="form-group"><label class="form-label">Mobile Number</label><input type="text" id="setupMobileNumber" class="form-input" value="+923254256177" required></div>
                <button type="submit" id="setupSubmitBtn" class="btn-primary" style="margin-top: 8px;"><span>💾 Save & Initialize Admin Profile</span></button>
            </form>
            <div style="margin-top: 16px;"><a href="#" onclick="showLoginView(); return false;" style="font-size: 12px; color: var(--cyan); text-decoration: none;">← Return to Login</a></div>
        </div>
    </div>

    <!-- 2. Step 1: Login Screen -->
    <div id="loginScreen" class="auth-screen">
        <div class="auth-box glass-panel">
            <div class="brand-badge">🔐 Official 1 — Admin Control Panel</div>
            <h1 id="loginTitleHeader" style="font-size: 22px; font-weight: 800; margin-bottom: 4px; color: #FFFFFF;">Admin Login</h1>
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 22px;">Authorized Credentials Gateway</p>
            <div id="loginAlertBox" style="display: none; padding: 10px 14px; background: rgba(255,23,68,0.15); border: 1px solid rgba(255,23,68,0.35); border-radius: var(--radius-sm); color: #FF6B8B; font-size: 12.5px; margin-bottom: 16px; text-align: left;"></div>
            <form id="loginForm" onsubmit="handleLoginSubmit(event)">
                <div class="form-group"><label class="form-label">Admin ID / Username</label><input type="text" id="loginIdOrUsername" class="form-input" value="565656565666555" required autocomplete="username"></div>
                <div class="form-group"><label class="form-label">Password</label><input type="password" id="loginPassword" class="form-input" value="bismajan56b@${'$'}56" required autocomplete="current-password"></div>
                <button type="submit" id="loginBtn" class="btn-primary" style="margin-top: 8px;"><span>🔐 Login & Proceed to Verification</span></button>
            </form>
            <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                <a href="#" onclick="showSetupView(); return false;" style="font-size: 12px; color: var(--gold); text-decoration: none;">⚙️ Admin Profile Setup</a>
                <span style="font-size: 11px; color: var(--text-muted);">Protected by 2FA & SHA-256</span>
            </div>
        </div>
    </div>

    <!-- 3. Step 2: Mobile Number Verification Screen (WhatsApp OTP) -->
    <div id="verifyScreen" class="auth-screen" style="display: none;">
        <div class="auth-box glass-panel">
            <div class="brand-badge" style="background: rgba(0, 230, 118, 0.12); border-color: rgba(0, 230, 118, 0.35); color: var(--emerald);">📲 WhatsApp Verification</div>
            <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 4px; color: #FFFFFF;">Mobile Number Verification</h2>
            <p style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 20px;">Two-factor authentication required. Code dispatched via authorized WhatsApp service.</p>
            <div id="verifyAlertBox" style="display: none; padding: 10px 14px; border-radius: var(--radius-sm); font-size: 12.5px; margin-bottom: 16px; text-align: left;"></div>
            <div class="form-group">
                <label class="form-label">Mobile Number</label>
                <div style="display: flex; gap: 8px;">
                    <input type="text" id="verifyMobileInput" class="form-input" value="+923254256177" required>
                    <button type="button" id="sendCodeBtn" onclick="handleSendWhatsAppOtp()" class="btn-secondary" style="white-space: nowrap; font-weight: 700;"><span>📲 Send Verification Code</span></button>
                </div>
            </div>
            <div id="otpInputSection" style="margin-top: 20px;">
                <label class="form-label" style="text-align: center;">Verification Code</label>
                <div class="otp-input-wrap"><input type="text" id="verifyOtpCode" class="otp-large-input" maxlength="6" placeholder="______" autocomplete="one-time-code"></div>
                <div id="cooldownTimerWrap" style="display: none; font-size: 12px; color: var(--warning); margin-bottom: 12px;">⏳ Resend code available in: <strong id="cooldownTimerText">60s</strong></div>
                <button type="button" id="verifyOtpBtn" onclick="handleVerifyWhatsAppOtp()" class="btn-primary" style="margin-top: 6px;"><span>✅ Verify & Continue</span></button>
            </div>
            <div style="margin-top: 18px;"><a href="#" onclick="showLoginView(); return false;" style="font-size: 12px; color: var(--text-muted); text-decoration: none;">← Back to Login</a></div>
        </div>
    </div>
"""

    private const val HTML_BODY_APP = """
    <!-- 4. Official 1 Dashboard & Admin App (15 Sections) -->
    <div id="adminApp">
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-logo">🛡️</div>
                <div>
                    <h3 id="sidebarPanelTitle" style="font-size: 15px; font-weight: 800; color: #FFFFFF;">OFFICIAL 1</h3>
                    <p style="font-size: 11px; color: var(--gold); font-weight: 700;">ADMIN CONTROL PANEL</p>
                </div>
            </div>
            <div class="sidebar-menu">
                <div class="menu-category">Main Overview</div>
                <a class="menu-item active" onclick="switchTab('dashboard')"><span class="menu-item-icon">📊</span> Dashboard</a>
                <div class="menu-category">Core Operations</div>
                <a class="menu-item" onclick="switchTab('users')"><span class="menu-item-icon">👥</span> User Management</a>
                <a class="menu-item" onclick="switchTab('admins')"><span class="menu-item-icon">👑</span> Admin Management</a>
                <a class="menu-item" onclick="switchTab('rooms')"><span class="menu-item-icon">🎙️</span> Room Management</a>
                <div class="menu-category">Ecosystem & Revenue</div>
                <a class="menu-item" onclick="switchTab('agencies')"><span class="menu-item-icon">🏢</span> Agency Management</a>
                <a class="menu-item" onclick="switchTab('bd')"><span class="menu-item-icon">💼</span> BD Management</a>
                <a class="menu-item" onclick="switchTab('coins')"><span class="menu-item-icon">🪙</span> Coin/Diamond Management</a>
                <a class="menu-item" onclick="switchTab('withdrawals')"><span class="menu-item-icon">💳</span> Withdrawal Management</a>
                <a class="menu-item" onclick="switchTab('gifts')"><span class="menu-item-icon">🎁</span> Gift & Frame Management</a>
                <div class="menu-category">Safety & Communication</div>
                <a class="menu-item" onclick="switchTab('reports')"><span class="menu-item-icon">🚨</span> Reports</a>
                <a class="menu-item" onclick="switchTab('notifications')"><span class="menu-item-icon">🔔</span> Notifications</a>
                <div class="menu-category">System Configuration</div>
                <a class="menu-item" onclick="switchTab('app_settings')"><span class="menu-item-icon">⚙️</span> App Settings</a>
                <a class="menu-item" onclick="switchTab('admin_profile')"><span class="menu-item-icon">👤</span> Admin Profile</a>
                <a class="menu-item" onclick="switchTab('security')"><span class="menu-item-icon">🔒</span> Security Settings</a>
                <a class="menu-item" onclick="handleLogout()" style="color: var(--danger);"><span class="menu-item-icon">🚪</span> Logout</a>
            </div>
            <div class="sidebar-footer">
                <div style="display: flex; align-items: center; gap: 10px;">
                    <div style="width: 34px; height: 34px; border-radius: 50%; background: #FF2A85; display: flex; align-items: center; justify-content: center; font-weight: 800; color: white;">👑</div>
                    <div style="flex: 1; overflow: hidden;">
                        <div id="sidebarAdminName" style="font-size: 12.5px; font-weight: 700; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Sherry</div>
                        <div id="sidebarAdminRole" class="badge-role role-super-admin" style="display: inline-block; padding: 2px 6px; font-size: 9px; margin-top: 2px;">Super Admin</div>
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
                        <p id="currentTabSubtitle">Real-time system telemetry and platform oversight</p>
                    </div>
                </div>
                <div class="topbar-actions">
                    <div id="topbarStatusBadge" class="badge-role role-super-admin">2FA SECURED</div>
                    <button class="btn-secondary" onclick="refreshCurrentTab()" title="Refresh Data">🔄 Refresh</button>
                </div>
            </header>

            <div class="content-area">
                <!-- 1. DASHBOARD -->
                <section id="tab-dashboard" class="tab-content active">
                    <div class="stats-grid">
                        <div class="stat-card"><div class="stat-card-title">Total Users</div><div class="stat-card-value" id="statTotalUsers">0</div><div class="stat-card-icon">👥</div></div>
                        <div class="stat-card"><div class="stat-card-title">Active Voice Rooms</div><div class="stat-card-value" id="statActiveRooms" style="color: var(--emerald);">0</div><div class="stat-card-icon">🎙️</div></div>
                        <div class="stat-card"><div class="stat-card-title">Admins / Roles</div><div class="stat-card-value" id="statAdminsCount" style="color: var(--gold);">0</div><div class="stat-card-icon">👑</div></div>
                        <div class="stat-card"><div class="stat-card-title">Pending Reports</div><div class="stat-card-value" id="statPendingReports" style="color: var(--danger);">0</div><div class="stat-card-icon">🚨</div></div>
                    </div>
                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 20px;">
                        <div class="glass-card" style="padding: 20px;">
                            <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 14px; color: var(--gold);">🛡️ Recent Security Audit Trail</h3>
                            <div class="table-responsive" style="margin-top: 0;">
                                <table>
                                    <thead><tr><th>Timestamp</th><th>Admin</th><th>Action</th><th>Target</th><th>Status</th></tr></thead>
                                    <tbody id="dashboardLogsTableBody"><tr><td colspan="5" style="text-align: center; color: var(--text-muted);">Loading audit logs...</td></tr></tbody>
                                </table>
                            </div>
                        </div>
                        <div class="glass-card" style="padding: 20px;">
                            <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 14px;">⚡ Quick Actions</h3>
                            <div style="display: flex; flex-direction: column; gap: 10px;">
                                <button class="btn-secondary" onclick="switchTab('users')" style="justify-content: flex-start;">👥 Manage Users & Balances</button>
                                <button class="btn-secondary" onclick="switchTab('rooms')" style="justify-content: flex-start;">🎙️ Voice Rooms Moderation</button>
                                <button class="btn-secondary" onclick="switchTab('notifications')" style="justify-content: flex-start;">🔔 Send System Broadcast</button>
                                <button class="btn-secondary" onclick="switchTab('admin_profile')" style="justify-content: flex-start;">👤 Edit Admin Profile</button>
                                <button class="btn-secondary" onclick="switchTab('security')" style="justify-content: flex-start;">🔒 Security & WhatsApp Gateway</button>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- 2. USERS -->
                <section id="tab-users" class="tab-content">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; gap: 12px; flex-wrap: wrap;">
                        <input type="text" id="userSearchInput" class="form-input" style="max-width: 320px;" placeholder="Search users..." oninput="debounceUserSearch()">
                        <select id="userRoleFilter" class="form-input" style="width: 160px;" onchange="loadUsers()">
                            <option value="">All Roles</option><option value="Super Admin">Super Admin</option><option value="Admin">Admin</option><option value="Manager">Manager</option><option value="BD">BD</option><option value="Agency">Agency</option><option value="Coin Reseller">Coin Reseller</option><option value="User">User</option>
                        </select>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>User</th><th>Admin ID</th><th>Role</th><th>Coins / Diamonds</th><th>Status</th><th>Actions</th></tr></thead>
                            <tbody id="usersTableBody"><tr><td colspan="6" style="text-align: center;">Loading users...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 3. ADMINS -->
                <section id="tab-admins" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>Admin</th><th>ID</th><th>Assigned Role</th><th>Permissions</th><th>Actions</th></tr></thead>
                            <tbody id="adminsTableBody"><tr><td colspan="5" style="text-align: center;">Loading admin roles...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 4. ROOMS -->
                <section id="tab-rooms" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>Room Title</th><th>Owner</th><th>Category</th><th>Online Seats</th><th>Status</th><th>Actions</th></tr></thead>
                            <tbody id="roomsTableBody"><tr><td colspan="6" style="text-align: center;">Loading rooms...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 5. AGENCIES -->
                <section id="tab-agencies" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>Agency Name</th><th>Agency Code</th><th>Owner</th><th>Host Members</th><th>Level & Income</th><th>Ranking</th></tr></thead>
                            <tbody id="agenciesTableBody"><tr><td colspan="6" style="text-align: center;">Loading agencies...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 6. BD -->
                <section id="tab-bd" class="tab-content">
                    <div class="glass-card" style="padding: 20px;">
                        <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 8px;">💼 Business Development Portfolio</h3>
                        <div id="bdListContainer"></div>
                    </div>
                </section>

                <!-- 7. COINS -->
                <section id="tab-coins" class="tab-content">
                    <div class="glass-card" style="padding: 20px; max-width: 600px;">
                        <h3 style="font-size: 16px; font-weight: 800; color: var(--gold); margin-bottom: 14px;">🪙 Direct User Balance Adjustment</h3>
                        <form onsubmit="handleDirectBalanceSubmit(event)">
                            <div class="form-group"><label class="form-label">User ID</label><input type="text" id="balanceUserId" class="form-input" placeholder="e.g. 565656565666555" required></div>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                                <div class="form-group"><label class="form-label">Delta Coins (+/-)</label><input type="number" id="balanceDeltaCoins" class="form-input" value="0" required></div>
                                <div class="form-group"><label class="form-label">Delta Diamonds (+/-)</label><input type="number" id="balanceDeltaDiamonds" class="form-input" value="0" required></div>
                            </div>
                            <div class="form-group"><label class="form-label">Reason / Justification</label><input type="text" id="balanceReason" class="form-input" placeholder="e.g. Official Event Reward" required></div>
                            <button type="submit" class="btn-primary">Apply Balance Adjustment</button>
                        </form>
                    </div>
                </section>

                <!-- 8. WITHDRAWALS -->
                <section id="tab-withdrawals" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>Transaction ID</th><th>User ID</th><th>Type</th><th>Amount</th><th>Timestamp</th><th>Status</th><th>Action</th></tr></thead>
                            <tbody id="withdrawalsTableBody"><tr><td colspan="7" style="text-align: center;">Loading withdrawals...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 9. GIFTS -->
                <section id="tab-gifts" class="tab-content"><div id="giftsGrid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px;"></div></section>

                <!-- 10. REPORTS -->
                <section id="tab-reports" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead><tr><th>Reporter</th><th>Target</th><th>Reason</th><th>Status</th><th>Action</th></tr></thead>
                            <tbody id="reportsTableBody"><tr><td colspan="5" style="text-align: center;">Loading reports...</td></tr></tbody>
                        </table>
                    </div>
                </section>

                <!-- 11. NOTIFICATIONS -->
                <section id="tab-notifications" class="tab-content">
                    <div class="glass-card" style="padding: 24px; max-width: 650px;">
                        <h3 style="font-size: 16px; font-weight: 800; color: var(--cyan); margin-bottom: 14px;">🔔 Broadcast Push Notification</h3>
                        <form onsubmit="handleNotificationSubmit(event)">
                            <div class="form-group"><label class="form-label">Recipient User ID (Leave blank for ALL)</label><input type="text" id="notifTargetId" class="form-input" placeholder="Leave empty for all users"></div>
                            <div class="form-group"><label class="form-label">Notification Title</label><input type="text" id="notifTitle" class="form-input" value="Official 1 Notice 📢" required></div>
                            <div class="form-group"><label class="form-label">Message Content</label><textarea id="notifMessage" class="form-input" rows="4" placeholder="Enter message to dispatch..." required></textarea></div>
                            <button type="submit" class="btn-primary">🚀 Send Notification</button>
                        </form>
                    </div>
                </section>

                <!-- 12. APP SETTINGS -->
                <section id="tab-app_settings" class="tab-content"><div class="glass-card" style="padding: 24px;"><h3 style="font-size: 16px; font-weight: 800; margin-bottom: 16px;">⚙️ Feature Configuration</h3><div id="configsListContainer" style="display: flex; flex-direction: column; gap: 14px;"></div></div></section>

                <!-- 13. ADMIN PROFILE -->
                <section id="tab-admin_profile" class="tab-content">
                    <div class="glass-card" style="padding: 24px; max-width: 650px;">
                        <h3 style="font-size: 16px; font-weight: 800; color: var(--gold); margin-bottom: 6px;">👤 Official 1 — Admin Profile Details</h3>
                        <p style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 20px;">Edit panel name, admin ID, password, and mobile number securely.</p>
                        <form onsubmit="handleProfileUpdateSubmit(event)">
                            <div class="form-group"><label class="form-label">Panel Name</label><input type="text" id="profilePanelName" class="form-input" required></div>
                            <div class="form-group"><label class="form-label">Admin / User Name</label><input type="text" id="profileAdminName" class="form-input" required></div>
                            <div class="form-group"><label class="form-label">Admin ID</label><input type="text" id="profileAdminId" class="form-input" required></div>
                            <div class="form-group"><label class="form-label">New Password (Leave blank to keep existing)</label><input type="password" id="profileNewPassword" class="form-input" placeholder="Enter new password"></div>
                            <div class="form-group"><label class="form-label">Registered 2FA Mobile Number</label><input type="text" id="profileMobileNumber" class="form-input" required></div>
                            <button type="submit" class="btn-primary" style="margin-top: 8px;">💾 Save Admin Profile Changes</button>
                        </form>
                    </div>
                </section>

                <!-- 14. SECURITY -->
                <section id="tab-security" class="tab-content">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                        <div class="glass-card" style="padding: 24px;">
                            <h3 style="font-size: 16px; font-weight: 800; color: var(--emerald); margin-bottom: 14px;">🔒 WhatsApp OTP Gateway Config</h3>
                            <form onsubmit="handleSecuritySettingsSubmit(event)">
                                <div class="form-group"><label class="form-label">WhatsApp Gateway API URL</label><input type="text" id="secWhatsAppUrl" class="form-input" placeholder="e.g. https://api.whatsapp.com/v1/messages"></div>
                                <div class="form-group"><label class="form-label">WhatsApp Gateway API Key</label><input type="password" id="secWhatsAppKey" class="form-input" placeholder="API Secret Key"></div>
                                <div class="form-group" style="display: flex; align-items: center; gap: 10px;">
                                    <input type="checkbox" id="sec2FaEnforced" checked style="width: 18px; height: 18px;">
                                    <label for="sec2FaEnforced" style="font-size: 13px; font-weight: 600; cursor: pointer;">Enforce WhatsApp 2FA on every login</label>
                                </div>
                                <button type="submit" class="btn-primary">💾 Update Security Settings</button>
                            </form>
                        </div>
                        <div class="glass-card" style="padding: 24px;">
                            <h3 style="font-size: 16px; font-weight: 800; margin-bottom: 12px;">🛡️ Security Protocols</h3>
                            <ul style="font-size: 13px; color: var(--text-secondary); line-height: 1.8; padding-left: 18px;">
                                <li><strong>SHA-256 Hashing:</strong> Passwords stored strictly as cryptographic digests.</li>
                                <li><strong>WhatsApp OTP 2FA:</strong> Server-side generated 6-digit codes with 5-min TTL.</li>
                                <li><strong>Rate Limiting:</strong> Max 5 failed logins before temporary lockout cooldown.</li>
                                <li><strong>Session Invalidation:</strong> Immediate token revocation on logout.</li>
                            </ul>
                        </div>
                    </div>
                    <div class="glass-card" style="padding: 20px; margin-top: 24px;">
                        <h3 style="font-size: 15px; font-weight: 700; margin-bottom: 12px;">📜 Full Security Audit Trail</h3>
                        <div class="table-responsive">
                            <table>
                                <thead><tr><th>Timestamp</th><th>Admin ID</th><th>Admin Name</th><th>Action</th><th>Target</th><th>Details</th><th>IP Address</th></tr></thead>
                                <tbody id="securityLogsTableBody"><tr><td colspan="7" style="text-align: center;">Loading logs...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>
            </div>
        </main>
    </div>

    <div id="roleModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header"><h3>Assign Admin Role</h3><button class="close-btn" onclick="closeModal('roleModal')">✕</button></div>
            <form onsubmit="handleRoleModalSubmit(event)">
                <input type="hidden" id="modalTargetUserId">
                <div class="form-group"><label class="form-label">Target User</label><div id="modalTargetUserName" style="font-weight: 700; color: #FFF; font-size: 14px; margin-bottom: 6px;"></div></div>
                <div class="form-group"><label class="form-label">Select Role</label>
                    <select id="modalRoleSelect" class="form-input">
                        <option value="Super Admin">Super Admin (Level 100)</option><option value="Admin">Admin (Level 80)</option><option value="Manager">Manager (Level 60)</option><option value="BD">BD (Level 50)</option><option value="Agency">Agency (Level 40)</option><option value="Coin Reseller">Coin Reseller (Level 30)</option><option value="User">User (Standard)</option>
                    </select>
                </div>
                <div class="form-group"><label class="form-label">Audit Notes</label><input type="text" id="modalRoleNotes" class="form-input" placeholder="Justification for role change"></div>
                <button type="submit" class="btn-primary" style="margin-top: 10px;">Save Role</button>
            </form>
        </div>
    </div>
    <div id="toastContainer" class="toast-container"></div>
"""

    private const val JS_AUTH = """
    <script>
        let currentPreAuthToken = '';
        let currentAdminSession = null;
        let cooldownInterval = null;

        document.addEventListener('DOMContentLoaded', () => {
            fetchInitialInfo();
        });

        async function fetchInitialInfo() {
            try {
                const res = await fetch('/api/admin/info');
                const data = await res.json();
                if (data.success && data.panelName) {
                    document.title = data.panelName + ' — Admin Control Panel';
                    document.getElementById('loginTitleHeader').innerText = data.panelName + ' Login';
                    document.getElementById('setupPanelName').value = data.panelName;
                }
            } catch (e) {
                console.warn('Could not fetch initial info:', e);
            }
        }

        function showToast(msg, type = 'info') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.innerText = (type === 'success' ? '✅ ' : type === 'error' ? '❌ ' : 'ℹ️ ') + msg;
            container.appendChild(toast);
            setTimeout(() => { toast.remove(); }, 3500);
        }

        function showSetupView() {
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('setupScreen').style.display = 'flex';
        }

        function showLoginView() {
            document.getElementById('setupScreen').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('loginScreen').style.display = 'flex';
        }

        function showVerifyView() {
            document.getElementById('setupScreen').style.display = 'none';
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'flex';
        }

        function showAdminDashboard() {
            document.getElementById('setupScreen').style.display = 'none';
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('verifyScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'block';
            switchTab('dashboard');
        }

        async function handleSetupSubmit(e) {
            e.preventDefault();
            const panelName = document.getElementById('setupPanelName').value.trim();
            const adminName = document.getElementById('setupAdminName').value.trim();
            const adminId = document.getElementById('setupAdminId').value.trim();
            const password = document.getElementById('setupPassword').value.trim();
            const mobileNumber = document.getElementById('setupMobileNumber').value.trim();

            const btn = document.getElementById('setupSubmitBtn');
            btn.disabled = true;
            try {
                const res = await fetch('/api/admin/setup', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ panelName, adminName, adminId, password, mobileNumber })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Admin Profile Initialized Successfully!', 'success');
                    document.getElementById('loginIdOrUsername').value = adminId;
                    document.getElementById('loginPassword').value = password;
                    showLoginView();
                } else {
                    showToast(data.message || 'Setup failed', 'error');
                }
            } catch (err) {
                showToast('Network error during setup', 'error');
            } finally {
                btn.disabled = false;
            }
        }

        async function handleLoginSubmit(e) {
            e.preventDefault();
            const idOrUsername = document.getElementById('loginIdOrUsername').value.trim();
            const password = document.getElementById('loginPassword').value.trim();
            const alertBox = document.getElementById('loginAlertBox');
            const btn = document.getElementById('loginBtn');

            alertBox.style.display = 'none';
            btn.disabled = true;
            try {
                const res = await fetch('/api/admin/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ idOrUsername, password })
                });
                const data = await res.json();
                if (data.success && data.step === 'OTP_REQUIRED') {
                    currentPreAuthToken = data.preAuthToken;
                    document.getElementById('verifyMobileInput').value = data.mobileNumber || '+923254256177';
                    showToast('Credentials verified. Please complete WhatsApp verification.', 'info');
                    showVerifyView();
                } else {
                    alertBox.innerText = data.message || 'Invalid Admin ID / Username or Password.';
                    alertBox.style.display = 'block';
                    showToast(data.message || 'Login failed', 'error');
                }
            } catch (err) {
                alertBox.innerText = 'Network error connecting to Admin Server.';
                alertBox.style.display = 'block';
                showToast('Connection failed', 'error');
            } finally {
                btn.disabled = false;
            }
        }

        async function handleSendWhatsAppOtp() {
            if (!currentPreAuthToken) {
                showToast('Session expired. Please login again.', 'error');
                showLoginView();
                return;
            }
            const mobileNumber = document.getElementById('verifyMobileInput').value.trim();
            const sendBtn = document.getElementById('sendCodeBtn');
            const alertBox = document.getElementById('verifyAlertBox');
            sendBtn.disabled = true;

            try {
                const res = await fetch('/api/admin/send-whatsapp-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ preAuthToken: currentPreAuthToken, mobileNumber })
                });
                const data = await res.json();
                if (data.success) {
                    alertBox.style.display = 'block';
                    alertBox.style.background = 'rgba(0, 230, 118, 0.15)';
                    alertBox.style.borderColor = 'rgba(0, 230, 118, 0.35)';
                    alertBox.style.color = '#00E676';
                    alertBox.innerText = '✅ ' + data.message;
                    showToast('WhatsApp OTP dispatched successfully!', 'success');
                    startCooldownTimer(data.cooldownSeconds || 60);
                } else {
                    alertBox.style.display = 'block';
                    alertBox.style.background = 'rgba(255, 23, 68, 0.15)';
                    alertBox.style.borderColor = 'rgba(255, 23, 68, 0.35)';
                    alertBox.style.color = '#FF6B8B';
                    alertBox.innerText = '❌ ' + (data.message || 'Failed to send OTP.');
                    showToast(data.message || 'Failed to dispatch code', 'error');
                    sendBtn.disabled = false;
                }
            } catch (err) {
                showToast('Network error while requesting code', 'error');
                sendBtn.disabled = false;
            }
        }

        function startCooldownTimer(seconds) {
            clearInterval(cooldownInterval);
            const wrap = document.getElementById('cooldownTimerWrap');
            const text = document.getElementById('cooldownTimerText');
            const sendBtn = document.getElementById('sendCodeBtn');
            wrap.style.display = 'block';
            sendBtn.disabled = true;
            let remaining = seconds;
            text.innerText = remaining + 's';

            cooldownInterval = setInterval(() => {
                remaining--;
                if (remaining <= 0) {
                    clearInterval(cooldownInterval);
                    wrap.style.display = 'none';
                    sendBtn.disabled = false;
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
                showToast('Please enter the 6-digit verification code.', 'warning');
                return;
            }
            verifyBtn.disabled = true;

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
                    document.getElementById('sidebarPanelTitle').innerText = currentAdminSession.panelName.toUpperCase();
                    document.getElementById('sidebarAdminName').innerText = currentAdminSession.username;
                    document.getElementById('sidebarAdminRole').innerText = currentAdminSession.role;
                    showToast('Verified successfully! Gateway unlocked.', 'success');
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
        function switchTab(tabId) {
            document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));

            const targetContent = document.getElementById('tab-' + tabId);
            if (targetContent) targetContent.classList.add('active');

            const menuEl = Array.from(document.querySelectorAll('.menu-item')).find(el => el.getAttribute('onclick')?.includes(tabId));
            if (menuEl) menuEl.classList.add('active');

            const titleMap = {
                dashboard: ['Dashboard', 'Real-time system telemetry and platform oversight'],
                users: ['User Management', 'Inspect users, assign roles, adjust balances & enforce moderation'],
                admins: ['Admin Management', 'Role hierarchy and granular administrative permissions'],
                rooms: ['Room Management', 'Active voice chat rooms, seat occupancies & live moderation'],
                agencies: ['Agency Management', 'Agency directories, host members count & revenue statistics'],
                bd: ['BD Management', 'Business development partners and agency portfolios'],
                coins: ['Coin & Diamond Management', 'Wallet balances and direct administrative adjustments'],
                withdrawals: ['Withdrawal Management', 'Review host cashout requests and approve/reject'],
                gifts: ['Gift & Frame Management', 'Virtual gifts catalog, avatar frames & animations'],
                reports: ['Moderation Reports', 'Review violations, resolve user tickets & enforce rules'],
                notifications: ['Broadcast Notifications', 'Dispatch system notifications to platform users'],
                app_settings: ['App Settings', 'Application branding, feature switches and maintenance mode'],
                admin_profile: ['Admin Profile', 'Configure Official 1 credentials, password and mobile number'],
                security: ['Security Settings', 'WhatsApp OTP gateway configuration, 2FA and audit logs']
            };

            const info = titleMap[tabId] || ['Control Panel', 'Official 1 Administration'];
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
            switch(tabId) {
                case 'dashboard': loadDashboardStats(); break;
                case 'users': loadUsers(); break;
                case 'admins': loadAdmins(); break;
                case 'rooms': loadRooms(); break;
                case 'agencies': loadAgencies(); break;
                case 'bd': loadBD(); break;
                case 'withdrawals': loadWithdrawals(); break;
                case 'gifts': loadGifts(); break;
                case 'reports': loadReports(); break;
                case 'app_settings': loadAppConfigs(); break;
                case 'admin_profile': loadAdminProfileTab(); break;
                case 'security': loadSecuritySettings(); break;
            }
        }

        async function loadDashboardStats() {
            try {
                const res = await fetch('/api/admin/dashboard', { headers: getAuthHeaders() });
                const data = await res.json();
                document.getElementById('statTotalUsers').innerText = data.totalUsers || 0;
                document.getElementById('statActiveRooms').innerText = data.activeRooms || 0;
                document.getElementById('statAdminsCount').innerText = (data.superAdminsCount || 0) + (data.adminsCount || 0);
                document.getElementById('statPendingReports').innerText = data.pendingReports || 0;

                const tbody = document.getElementById('dashboardLogsTableBody');
                tbody.innerHTML = '';
                (data.recentLogs || []).forEach(log => {
                    const row = document.createElement('tr');
                    const d = new Date(log.timestamp).toLocaleTimeString();
                    row.innerHTML = `
                        <td style="font-family: monospace; font-size: 11px;">${'$'}{d}</td>
                        <td><strong>${'$'}{log.adminName}</strong></td>
                        <td><span class="badge-role role-admin">${'$'}{log.action}</span></td>
                        <td>${'$'}{log.targetName || log.targetId}</td>
                        <td><span style="color: ${'$'}{log.isSuccess ? 'var(--emerald)' : 'var(--danger)'}">${'$'}{log.isSuccess ? 'SUCCESS' : 'FAILED'}</span></td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        let searchTimeout = null;
        function debounceUserSearch() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(loadUsers, 300);
        }

        async function loadUsers() {
            const query = document.getElementById('userSearchInput')?.value || '';
            const role = document.getElementById('userRoleFilter')?.value || '';
            try {
                const res = await fetch(`/api/admin/users?query=${'$'}{encodeURIComponent(query)}&role=${'$'}{encodeURIComponent(role)}`, { headers: getAuthHeaders() });
                const users = await res.json();
                const tbody = document.getElementById('usersTableBody');
                tbody.innerHTML = '';
                users.forEach(u => {
                    const row = document.createElement('tr');
                    const roleClass = 'role-' + (u.role || 'user').toLowerCase().replace(' ', '-');
                    row.innerHTML = `
                        <td><div class="user-cell"><img src="${'$'}{u.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100'}" class="avatar-sm"><div><div>${'$'}{u.username}</div><div style="font-size: 11px; color: var(--text-muted);">Lv.${'$'}{u.userLevel} • VIP ${'$'}{u.vipLevel}</div></div></div></td>
                        <td style="font-family: monospace; color: var(--cyan);">${'$'}{u.id}</td>
                        <td><span class="badge-role ${'$'}{roleClass}">${'$'}{u.role}</span></td>
                        <td>🪙 ${'$'}{u.coins?.toLocaleString()} / 💎 ${'$'}{u.diamonds?.toLocaleString()}</td>
                        <td><span style="color: ${'$'}{u.isBanned ? 'var(--danger)' : 'var(--emerald)'}; font-weight: 700;">${'$'}{u.isBanned ? 'BANNED' : 'ACTIVE'}</span></td>
                        <td>
                            <button class="btn-secondary" onclick="openRoleModal('${'$'}{u.id}', '${'$'}{u.username}', '${'$'}{u.role}')" style="padding: 4px 8px; font-size: 11px;">Role</button>
                            <button class="${'$'}{u.isBanned ? 'btn-success' : 'btn-danger'}" onclick="toggleBanUser('${'$'}{u.id}', ${'$'}{!u.isBanned})" style="padding: 4px 8px; font-size: 11px;">${'$'}{u.isBanned ? 'Unban' : 'Ban'}</button>
                        </td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function toggleBanUser(userId, shouldBan) {
            if (!confirm(`Are you sure you want to ${'$'}{shouldBan ? 'BAN' : 'UNBAN'} user ID ${'$'}{userId}?`)) return;
            try {
                const res = await fetch('/api/admin/users/ban', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ userId, isBanned: shouldBan, reason: 'Admin panel action' })
                });
                const data = await res.json();
                if (data.success) { showToast(data.message, 'success'); loadUsers(); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Action failed', 'error'); }
        }

        async function loadAdmins() {
            try {
                const res = await fetch('/api/admin/users', { headers: getAuthHeaders() });
                const all = await res.json();
                const admins = all.filter(u => u.role !== 'User');
                const tbody = document.getElementById('adminsTableBody');
                tbody.innerHTML = '';
                admins.forEach(u => {
                    const row = document.createElement('tr');
                    const roleClass = 'role-' + u.role.toLowerCase().replace(' ', '-');
                    row.innerHTML = `
                        <td><strong>${'$'}{u.username}</strong></td>
                        <td style="font-family: monospace; color: var(--cyan);">${'$'}{u.id}</td>
                        <td><span class="badge-role ${'$'}{roleClass}">${'$'}{u.role}</span></td>
                        <td style="font-size: 11px; color: var(--text-muted);">${'$'}{u.permissions || 'Standard Role Permissions'}</td>
                        <td><button class="btn-secondary" onclick="openRoleModal('${'$'}{u.id}', '${'$'}{u.username}', '${'$'}{u.role}')" style="padding: 4px 8px; font-size: 11px;">Edit Role</button></td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        function openRoleModal(userId, username, currentRole) {
            document.getElementById('modalTargetUserId').value = userId;
            document.getElementById('modalTargetUserName').innerText = username + ' (' + userId + ')';
            document.getElementById('modalRoleSelect').value = currentRole || 'User';
            document.getElementById('roleModal').style.display = 'flex';
        }
        function closeModal(id) { document.getElementById(id).style.display = 'none'; }

        async function handleRoleModalSubmit(e) {
            e.preventDefault();
            const userId = document.getElementById('modalTargetUserId').value;
            const role = document.getElementById('modalRoleSelect').value;
            const notes = document.getElementById('modalRoleNotes').value;
            try {
                const res = await fetch('/api/admin/users/role', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ userId, role, notes })
                });
                const data = await res.json();
                if (data.success) { showToast('Role updated successfully', 'success'); closeModal('roleModal'); loadUsers(); loadAdmins(); }
                else { showToast(data.message || 'Failed to update role', 'error'); }
            } catch (e) { showToast('Error saving role', 'error'); }
        }

        async function loadRooms() {
            try {
                const res = await fetch('/api/admin/rooms', { headers: getAuthHeaders() });
                const rooms = await res.json();
                const tbody = document.getElementById('roomsTableBody');
                tbody.innerHTML = '';
                rooms.forEach(r => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td><strong>${'$'}{r.title}</strong></td>
                        <td>${'$'}{r.ownerName} (${'$'}{r.ownerId})</td>
                        <td><span class="badge-role role-manager">${'$'}{r.category}</span></td>
                        <td>${'$'}{r.onlineCount} / ${'$'}{r.seatCount} seats</td>
                        <td><span style="color: ${'$'}{r.isActive ? 'var(--emerald)' : 'var(--danger)'}">${'$'}{r.isActive ? 'LIVE' : 'CLOSED'}</span></td>
                        <td><button class="btn-danger" onclick="closeVoiceRoom('${'$'}{r.id}')" style="padding: 4px 8px; font-size: 11px;">Terminate</button></td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function closeVoiceRoom(roomId) {
            if (!confirm(`Terminate voice room ${'$'}{roomId}?`)) return;
            try {
                const res = await fetch('/api/admin/rooms/action', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ roomId, action: 'CLOSE' })
                });
                const data = await res.json();
                if (data.success) { showToast('Room terminated', 'success'); loadRooms(); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Failed to close room', 'error'); }
        }

        async function loadAgencies() {
            try {
                const res = await fetch('/api/admin/agencies', { headers: getAuthHeaders() });
                const agencies = await res.json();
                const tbody = document.getElementById('agenciesTableBody');
                tbody.innerHTML = '';
                agencies.forEach(a => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td><strong>${'$'}{a.name}</strong></td>
                        <td style="font-family: monospace; color: var(--gold);">${'$'}{a.agencyCode}</td>
                        <td>${'$'}{a.ownerName}</td>
                        <td>👥 ${'$'}{a.memberCount} hosts</td>
                        <td>Level ${'$'}{a.level} (💰 ${'$'}{a.totalIncome?.toLocaleString()})</td>
                        <td>#${'$'}{a.ranking}</td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function loadBD() {
            try {
                const res = await fetch('/api/admin/agencies', { headers: getAuthHeaders() });
                const agencies = await res.json();
                const container = document.getElementById('bdListContainer');
                container.innerHTML = `
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 14px;">
                        <div class="stat-card"><div class="stat-card-title">Total Active Agencies</div><div class="stat-card-value">${'$'}{agencies.length}</div></div>
                        <div class="stat-card"><div class="stat-card-title">Total Agency Hosts</div><div class="stat-card-value">${'$'}{agencies.reduce((acc, a) => acc + (a.memberCount || 0), 0)}</div></div>
                    </div>
                `;
            } catch (e) { console.error(e); }
        }

        async function handleDirectBalanceSubmit(e) {
            e.preventDefault();
            const userId = document.getElementById('balanceUserId').value.trim();
            const coins = parseInt(document.getElementById('balanceDeltaCoins').value || '0', 10);
            const diamonds = parseInt(document.getElementById('balanceDeltaDiamonds').value || '0', 10);
            const reason = document.getElementById('balanceReason').value.trim();
            try {
                const res = await fetch('/api/admin/users/balance', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ userId, coins, diamonds, reason })
                });
                const data = await res.json();
                if (data.success) { showToast('Balance updated successfully!', 'success'); document.getElementById('balanceDeltaCoins').value = '0'; document.getElementById('balanceDeltaDiamonds').value = '0'; }
                else { showToast(data.message || 'Failed to update balance', 'error'); }
            } catch (e) { showToast('Error applying balance change', 'error'); }
        }

        async function loadWithdrawals() {
            try {
                const res = await fetch('/api/admin/withdrawals', { headers: getAuthHeaders() });
                const list = await res.json();
                const tbody = document.getElementById('withdrawalsTableBody');
                tbody.innerHTML = '';
                list.forEach(tx => {
                    const row = document.createElement('tr');
                    const isPending = tx.status.includes('Pending');
                    row.innerHTML = `
                        <td style="font-family: monospace; font-size: 11px;">${'$'}{tx.id.take ? tx.id.take(8) : tx.id}</td>
                        <td>${'$'}{tx.userId}</td>
                        <td>${'$'}{tx.type}</td>
                        <td>🪙 ${'$'}{tx.amountCoins} / 💎 ${'$'}{tx.amountDiamonds}</td>
                        <td>${'$'}{new Date(tx.timestamp).toLocaleDateString()}</td>
                        <td><span class="badge-role ${'$'}{isPending ? 'role-warning' : 'role-bd'}">${'$'}{tx.status}</span></td>
                        <td>
                            ${'$'}{isPending ? `
                                <button class="btn-success" onclick="processWithdrawal('${'$'}{tx.id}', 'APPROVE')" style="padding: 3px 8px; font-size: 11px;">Approve</button>
                                <button class="btn-danger" onclick="processWithdrawal('${'$'}{tx.id}', 'REJECT')" style="padding: 3px 8px; font-size: 11px;">Reject</button>
                            ` : '<span style="color: var(--text-muted); font-size: 11px;">Processed</span>'}
                        </td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function processWithdrawal(txId, action) {
            try {
                const res = await fetch('/api/admin/withdrawals/action', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ txId, action, notes: 'Handled via Official 1 Web Panel' })
                });
                const data = await res.json();
                if (data.success) { showToast(`Withdrawal ${'$'}{action}D successfully`, 'success'); loadWithdrawals(); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Action failed', 'error'); }
        }

        async function loadGifts() {
            try {
                const res = await fetch('/api/admin/gifts', { headers: getAuthHeaders() });
                const items = await res.json();
                const grid = document.getElementById('giftsGrid');
                grid.innerHTML = '';
                items.forEach(item => {
                    const card = document.createElement('div');
                    card.className = 'glass-card';
                    card.style.padding = '14px';
                    card.style.textAlign = 'center';
                    card.innerHTML = `
                        <div style="font-size: 32px; margin-bottom: 8px;">🎁</div>
                        <div style="font-weight: 700; font-size: 13px;">${'$'}{item.name}</div>
                        <div style="color: var(--gold); font-size: 12px; margin-top: 4px;">🪙 ${'$'}{item.price}</div>
                        <div style="font-size: 10.5px; color: var(--text-muted); margin-top: 2px;">${'$'}{item.category}</div>
                    `;
                    grid.appendChild(card);
                });
            } catch (e) { console.error(e); }
        }

        async function loadReports() {
            try {
                const res = await fetch('/api/admin/reports', { headers: getAuthHeaders() });
                const reports = await res.json();
                const tbody = document.getElementById('reportsTableBody');
                tbody.innerHTML = '';
                reports.forEach(r => {
                    const row = document.createElement('tr');
                    const isPending = r.status === 'Pending';
                    row.innerHTML = `
                        <td>${'$'}{r.reporterName}</td>
                        <td>${'$'}{r.targetType}: <strong>${'$'}{r.targetTitleOrName}</strong></td>
                        <td>${'$'}{r.reason}</td>
                        <td><span class="badge-role ${'$'}{isPending ? 'role-admin' : 'role-bd'}">${'$'}{r.status}</span></td>
                        <td>
                            ${'$'}{isPending ? `
                                <button class="btn-success" onclick="resolveReportAction('${'$'}{r.id}', 'Resolved')" style="padding: 3px 8px; font-size: 11px;">Resolve</button>
                                <button class="btn-secondary" onclick="resolveReportAction('${'$'}{r.id}', 'Dismissed')" style="padding: 3px 8px; font-size: 11px;">Dismiss</button>
                            ` : `<span style="font-size: 11px; color: var(--text-muted);">${'$'}{r.status}</span>`}
                        </td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function resolveReportAction(reportId, status) {
            try {
                const res = await fetch('/api/admin/reports/resolve', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ reportId, status, notes: 'Resolved by Official 1' })
                });
                const data = await res.json();
                if (data.success) { showToast('Report updated', 'success'); loadReports(); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Failed to resolve report', 'error'); }
        }

        async function handleNotificationSubmit(e) {
            e.preventDefault();
            const targetUserId = document.getElementById('notifTargetId').value.trim();
            const title = document.getElementById('notifTitle').value.trim();
            const message = document.getElementById('notifMessage').value.trim();
            try {
                const res = await fetch('/api/admin/notifications', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ targetUserId, title, message })
                });
                const data = await res.json();
                if (data.success) { showToast('Notification broadcasted!', 'success'); document.getElementById('notifMessage').value = ''; }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Failed to dispatch notification', 'error'); }
        }

        async function loadAppConfigs() {
            try {
                const res = await fetch('/api/admin/configs', { headers: getAuthHeaders() });
                const configs = await res.json();
                const container = document.getElementById('configsListContainer');
                container.innerHTML = '';
                configs.forEach(c => {
                    const row = document.createElement('div');
                    row.style.display = 'flex';
                    row.style.alignItems = 'center';
                    row.style.justifyContent = 'space-between';
                    row.style.padding = '12px';
                    row.style.background = 'rgba(255,255,255,0.02)';
                    row.style.borderRadius = 'var(--radius-sm)';
                    row.style.border = '1px solid var(--border-color)';
                    row.innerHTML = `
                        <div><strong style="color: #FFF; font-size: 13.5px;">${'$'}{c.key}</strong><div style="font-size: 11px; color: var(--text-muted);">${'$'}{c.category}</div></div>
                        <div style="display: flex; gap: 8px;">
                            <input type="text" id="cfg_${'$'}{c.key}" class="form-input" value="${'$'}{c.value}" style="width: 280px; padding: 6px 10px; font-size: 12.5px;">
                            <button class="btn-secondary" onclick="saveAppConfig('${'$'}{c.key}', '${'$'}{c.category}')" style="padding: 6px 12px; font-size: 12px;">Save</button>
                        </div>
                    `;
                    container.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function saveAppConfig(key, category) {
            const input = document.getElementById('cfg_' + key);
            const value = input ? input.value : '';
            try {
                const res = await fetch('/api/admin/configs', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ key, value, category })
                });
                const data = await res.json();
                if (data.success) { showToast(`Config '${'$'}{key}' saved`, 'success'); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Failed to save config', 'error'); }
        }

        async function loadAdminProfileTab() {
            try {
                const res = await fetch('/api/admin/profile', { headers: getAuthHeaders() });
                const data = await res.json();
                if (data.success && data.profile) {
                    const p = data.profile;
                    document.getElementById('profilePanelName').value = p.panelName || 'Official 1';
                    document.getElementById('profileAdminName').value = p.adminName || 'Sherry';
                    document.getElementById('profileAdminId').value = p.adminId || '565656565666555';
                    document.getElementById('profileMobileNumber').value = p.mobileNumber || '+923254256177';
                }
            } catch (e) { console.error(e); }
        }

        async function handleProfileUpdateSubmit(e) {
            e.preventDefault();
            const panelName = document.getElementById('profilePanelName').value.trim();
            const adminName = document.getElementById('profileAdminName').value.trim();
            const adminId = document.getElementById('profileAdminId').value.trim();
            const password = document.getElementById('profileNewPassword').value.trim();
            const mobileNumber = document.getElementById('profileMobileNumber').value.trim();

            try {
                const res = await fetch('/api/admin/profile', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ panelName, adminName, adminId, password, mobileNumber })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Admin Profile updated successfully!', 'success');
                    document.getElementById('sidebarPanelTitle').innerText = panelName.toUpperCase();
                    document.getElementById('sidebarAdminName').innerText = adminName;
                    document.getElementById('profileNewPassword').value = '';
                } else { showToast(data.message || 'Update failed', 'error'); }
            } catch (e) { showToast('Error updating admin profile', 'error'); }
        }

        async function loadSecuritySettings() {
            try {
                const res = await fetch('/api/admin/profile', { headers: getAuthHeaders() });
                const data = await res.json();
                if (data.success && data.profile) {
                    document.getElementById('secWhatsAppUrl').value = data.profile.whatsappApiUrl || '';
                    document.getElementById('sec2FaEnforced').checked = (data.profile.is2FaEnforced !== false);
                }

                const logsRes = await fetch('/api/admin/audit-logs?limit=50', { headers: getAuthHeaders() });
                const logs = await logsRes.json();
                const tbody = document.getElementById('securityLogsTableBody');
                tbody.innerHTML = '';
                logs.forEach(l => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td style="font-family: monospace; font-size: 11px;">${'$'}{new Date(l.timestamp).toLocaleString()}</td>
                        <td style="font-family: monospace; color: var(--cyan);">${'$'}{l.adminId}</td>
                        <td><strong>${'$'}{l.adminName}</strong></td>
                        <td><span class="badge-role role-admin">${'$'}{l.action}</span></td>
                        <td>${'$'}{l.targetName || l.targetId}</td>
                        <td style="font-size: 11.5px; color: var(--text-secondary);">${'$'}{l.newValue || ''}</td>
                        <td style="font-family: monospace; font-size: 11px;">${'$'}{l.ipAddress}</td>
                    `;
                    tbody.appendChild(row);
                });
            } catch (e) { console.error(e); }
        }

        async function handleSecuritySettingsSubmit(e) {
            e.preventDefault();
            const whatsappApiUrl = document.getElementById('secWhatsAppUrl').value.trim();
            const whatsappApiKey = document.getElementById('secWhatsAppKey').value.trim();
            const is2FaEnforced = document.getElementById('sec2FaEnforced').checked;

            try {
                const res = await fetch('/api/admin/profile', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ whatsappApiUrl, whatsappApiKey, is2FaEnforced })
                });
                const data = await res.json();
                if (data.success) { showToast('Security Settings saved successfully!', 'success'); }
                else { showToast(data.message, 'error'); }
            } catch (e) { showToast('Error saving security settings', 'error'); }
        }

        function toggleSidebar() { document.getElementById('sidebar').classList.toggle('open'); }
    """

    private const val HTML_TAIL = """
    </script>
</body>
</html>
"""
}
