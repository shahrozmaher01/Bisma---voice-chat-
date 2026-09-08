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
    <title>Official Admin Panel</title>
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
        .auth-box { width: 100%; max-width: 460px; padding: 38px 32px; background: var(--bg-surface); border: 1px solid rgba(255, 42, 133, 0.25); border-radius: var(--radius-lg); box-shadow: 0 20px 50px rgba(0, 0, 0, 0.7); text-align: center; animation: fadeIn 0.3s ease; }
        .brand-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; background: rgba(255, 215, 0, 0.12); border: 1px solid rgba(255, 215, 0, 0.35); border-radius: 999px; color: var(--gold); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 16px; }
        .form-group { text-align: left; margin-bottom: 16px; }
        .form-label { display: block; font-size: 11.5px; font-weight: 700; color: var(--text-secondary); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
        .form-input { width: 100%; padding: 12px 14px; background: #151C2C; border: 1px solid var(--border-color); border-radius: var(--radius-sm); color: #FFFFFF; font-size: 14px; outline: none; transition: all 0.2s ease; }
        .form-input:focus { border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-glow); }
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
    <!-- Direct Admin Login Screen -->
    <div id="loginScreen" class="auth-screen">
        <div class="auth-box glass-panel">
            <div class="brand-badge">🛡️ SECURE GATEWAY</div>
            <h1 style="font-size: 24px; font-weight: 800; margin-bottom: 6px; color: #FFFFFF; letter-spacing: -0.5px;">Official Admin Panel</h1>
            <p style="font-size: 13px; color: var(--text-secondary); margin-bottom: 18px;">Administrative authentication and frame control gateway</p>
            
            <div style="background: rgba(0, 229, 255, 0.08); border: 1px solid rgba(0, 229, 255, 0.3); border-radius: var(--radius-sm); padding: 12px; margin-bottom: 16px; font-size: 12px; color: var(--text-secondary); text-align: left;">
                <div style="font-weight: 700; color: var(--cyan); margin-bottom: 4px;">👑 Authorized Official Credentials</div>
                <div>Username / ID: <strong style="color:#FFF;">Sherry</strong> (<span style="color:var(--gold);">565656565666555</span>)</div>
                <div>Password: <strong style="color:#FFF;">bismajan56b@$56</strong></div>
            </div>

            <button type="button" onclick="quickVerify()" class="btn-primary" style="background: linear-gradient(135deg, #00E5FF, #7C4DFF); margin-bottom: 16px; font-weight: 800; box-shadow: 0 0 15px rgba(0,229,255,0.4);">
                <span>⚡ 1-Click Instant Verify & Open Panel</span>
            </button>

            <div id="loginAlertBox" style="display: none; padding: 12px 16px; background: rgba(255,23,68,0.15); border: 1px solid rgba(255,23,68,0.4); border-radius: var(--radius-sm); color: #FF6B8B; font-size: 13px; font-weight: 600; margin-bottom: 18px; text-align: center;"></div>
            
            <form id="loginForm" onsubmit="handleLoginSubmit(event)" autocomplete="off">
                <div class="form-group">
                    <label class="form-label" for="loginUsernameOrId">Username or Admin ID</label>
                    <input type="text" id="loginUsernameOrId" class="form-input" placeholder="Enter Username or Admin ID" value="Sherry" required autocomplete="off">
                </div>
                <div class="form-group">
                    <label class="form-label" for="loginPassword">Password</label>
                    <input type="password" id="loginPassword" class="form-input" placeholder="Enter Password" value="bismajan56b@$56" required autocomplete="off">
                </div>
                <button type="submit" id="loginBtn" class="btn-primary" style="margin-top: 10px;"><span>🔐 Sign In / Verify</span></button>
            </form>
            <div style="margin-top: 22px; font-size: 11.5px; color: var(--text-muted); display: flex; align-items: center; justify-content: center; gap: 6px;">
                <span>🔒</span> Protected by Administrative Security Authentication
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
                <a class="menu-item" onclick="switchTab('frames')">
                    <span class="menu-item-icon">👑</span> <strong>Frame Management</strong>
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
                    <div id="topbarStatusBadge" class="badge-role role-super-admin">ADMIN VERIFIED 🛡️</div>
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

                <!-- 3. FRAME MANAGEMENT SECTION -->
                <section id="tab-frames" class="tab-content">
                    <!-- Send Frame Panel -->
                    <div class="glass-card" style="padding: 26px; margin-bottom: 24px;">
                        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; border-bottom: 1px solid var(--border-color); padding-bottom: 14px; flex-wrap: wrap; gap: 10px;">
                            <div>
                                <h3 style="font-size: 20px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                                    <span>👑</span> Frame Management
                                </h3>
                                <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                                    Official 1 Frame Sending System. Assign important official frames to a specific User ID with automated duration and expiration.
                                </p>
                            </div>
                            <div class="badge-role role-super-admin" style="font-size: 11px; padding: 6px 12px;">
                                ⭐ OFFICIAL 1 EXCLUSIVE
                            </div>
                        </div>

                        <!-- Section Heading: Send Frame -->
                        <div style="margin-bottom: 16px;">
                            <h4 style="font-size: 15px; font-weight: 700; color: var(--gold); display: flex; align-items: center; gap: 6px;">
                                <span>✨</span> Send Frame
                            </h4>
                            <p style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">
                                Select an official frame, specify the target User ID number, and define the validity duration.
                            </p>
                        </div>

                        <form id="sendFrameForm" onsubmit="handleSendOfficialFrame(event)" autocomplete="off">
                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 18px; margin-bottom: 18px;">
                                <!-- 1. Select Frame -->
                                <div class="form-group" style="margin-bottom: 0;">
                                    <label class="form-label" style="font-weight: 700; display: flex; justify-content: space-between;">
                                        <span>1. Select Frame *</span>
                                        <span style="font-size: 10.5px; color: var(--cyan);">Important Official Frames</span>
                                    </label>
                                    <select id="selectFrameInput" class="form-input" required onchange="onOfficialFrameChanged()">
                                        <option value="" disabled selected>-- [ Select Frame ▼ ] --</option>
                                        <option value="frame_official">Official</option>
                                        <option value="frame_manager">Manager</option>
                                        <option value="frame_super_admin">Super Admin</option>
                                        <option value="frame_admin">Admin</option>
                                        <option value="frame_admin_leader">Admin Leader</option>
                                        <option value="frame_bd">BD</option>
                                        <option value="frame_bd_leader">BD Leader</option>
                                        <option value="frame_agency">Agency</option>
                                        <option value="frame_agency_leader">Agency Leader</option>
                                        <option value="frame_host">Host</option>
                                        <option value="frame_coin_reseller">Coin Reseller</option>
                                        <option value="frame_super_coin_reseller">Super Coin Reseller</option>
                                        <option value="frame_cs">C's</option>
                                        <option value="frame_cs_leader">C's Leader</option>
                                    </select>
                                </div>

                                <!-- 2. Enter User ID -->
                                <div class="form-group" style="margin-bottom: 0;">
                                    <label class="form-label" style="font-weight: 700;">2. Enter User ID Number *</label>
                                    <input type="text" id="targetUserIdInput" class="form-input" placeholder="[ Enter User ID ]" required oninput="checkFrameConflictDebounced()">
                                    <div id="frameConflictNotice" style="display: none; font-size: 11px; margin-top: 6px; padding: 6px 10px; border-radius: 6px; line-height: 1.4;"></div>
                                </div>

                                <!-- 3. Select Days -->
                                <div class="form-group" style="margin-bottom: 0;">
                                    <label class="form-label" style="font-weight: 700;">3. Select Number of Days *</label>
                                    <select id="selectDaysInput" class="form-input" required>
                                        <option value="" disabled selected>-- [ Select Days ▼ ] --</option>
                                        <option value="1">1 Day</option>
                                        <option value="3">3 Days</option>
                                        <option value="7">7 Days (1 Week)</option>
                                        <option value="15">15 Days</option>
                                        <option value="30">30 Days (1 Month)</option>
                                        <option value="60">60 Days (2 Months)</option>
                                        <option value="90">90 Days (3 Months)</option>
                                        <option value="180">180 Days (Half Year)</option>
                                        <option value="365">365 Days (1 Year)</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Live Frame Visual Preview Card -->
                            <div id="framePreviewDisplay" style="background: rgba(0,0,0,0.38); border: 1px solid var(--border-color); border-radius: var(--radius-sm); padding: 14px 18px; margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 14px;">
                                <div style="display: flex; align-items: center; gap: 16px;">
                                    <div id="previewBadgeOrb" style="width: 52px; height: 52px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; border: 2px solid #FFD700; background: radial-gradient(circle, rgba(255,215,0,0.25) 0%, rgba(0,0,0,0.7) 100%); box-shadow: 0 0 14px rgba(255,215,0,0.35);">
                                        🛡️
                                    </div>
                                    <div>
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <span id="previewFrameTitle" style="font-size: 15px; font-weight: 800; color: #FFFFFF;">Select an Official Frame</span>
                                            <span id="previewBadgeTag" class="badge-role" style="font-size: 10px; padding: 2px 8px; background: rgba(255,215,0,0.2); color: #FFD700; border: 1px solid #FFD700;">OFFICIAL</span>
                                        </div>
                                        <div id="previewFrameDescription" style="font-size: 12px; color: var(--text-secondary); margin-top: 3px;">
                                            Important official frames can only be assigned by authorized Official 1 administrators.
                                        </div>
                                    </div>
                                </div>
                                <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 4px;">
                                    <span style="font-size: 10.5px; color: var(--text-muted); text-transform: uppercase;">System Safety Rule</span>
                                    <span style="font-size: 11.5px; font-weight: 700; color: var(--emerald);">✓ Conflict Prevention & Auto-Expiry Active</span>
                                </div>
                            </div>

                            <!-- 4. Send Frame Button -->
                            <div style="display: flex; justify-content: flex-end;">
                                <button type="submit" id="btnSendOfficialFrame" class="btn-primary" style="padding: 13px 34px; font-size: 14px; font-weight: 800; letter-spacing: 0.5px; width: auto; min-width: 190px;">
                                    <span>👑 SEND FRAME</span>
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Below this section: Frame Sending History -->
                    <div class="glass-card" style="padding: 26px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; flex-wrap: wrap; gap: 12px;">
                            <div>
                                <h3 style="font-size: 18px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                                    <span>📜</span> Frame Sending History
                                </h3>
                                <p style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">
                                    Real-time audit log of dispatched official frames, active validity, and auto-removal tracking
                                </p>
                            </div>
                            <div style="display: flex; gap: 10px; align-items: center;">
                                <input type="text" id="frameHistorySearch" class="form-input" style="max-width: 220px; font-size: 12px; padding: 7px 12px;" placeholder="Search User ID or Frame..." oninput="debounceFrameHistorySearch()">
                                <button class="btn-secondary" onclick="loadFrameHistory()" style="font-size: 12px; padding: 7px 14px; font-weight: 700;">
                                    🔄 Refresh History
                                </button>
                            </div>
                        </div>

                        <div class="table-responsive">
                            <table>
                                <thead>
                                    <tr>
                                        <th>User ID</th>
                                        <th>Frame Name</th>
                                        <th>Days</th>
                                        <th>Send Date</th>
                                        <th>Expiry Date</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="frameHistoryTableBody">
                                    <tr><td colspan="7" style="text-align: center; color: var(--text-muted);">Loading frame sending history...</td></tr>
                                </tbody>
                            </table>
                        </div>
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

        window.addEventListener('DOMContentLoaded', () => {
            const userInput = document.getElementById('loginUsernameOrId');
            const pwdInput = document.getElementById('loginPassword');
            if (userInput && !userInput.value) userInput.value = 'Sherry';
            if (pwdInput && !pwdInput.value) pwdInput.value = 'bismajan56b@$56';

            const savedToken = sessionStorage.getItem('adminToken');
            const urlParams = new URLSearchParams(window.location.search);
            if (savedToken) {
                verifyExistingSession(savedToken);
            } else if (urlParams.get('autologin') === '1') {
                quickVerify();
            } else {
                showLoginView();
            }
        });

        async function quickVerify() {
            const userInput = document.getElementById('loginUsernameOrId');
            const pwdInput = document.getElementById('loginPassword');
            if (userInput) userInput.value = 'Sherry';
            if (pwdInput) pwdInput.value = 'bismajan56b@$56';
            const fakeEvent = { preventDefault: () => {} };
            await handleLoginSubmit(fakeEvent);
        }

        function showToast(msg, type = 'info') {
            const container = document.getElementById('toastContainer');
            if (!container) return;
            const toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.innerHTML = (type === 'success' ? '✅ ' : (type === 'error' ? '❌ ' : 'ℹ️ ')) + msg;
            container.appendChild(toast);
            setTimeout(() => { toast.remove(); }, 3500);
        }

        function showLoginView() {
            document.getElementById('loginScreen').style.display = 'flex';
            document.getElementById('adminApp').style.display = 'none';
            const alertBox = document.getElementById('loginAlertBox');
            if (alertBox) alertBox.style.display = 'none';
        }

        function showAdminDashboard() {
            document.getElementById('loginScreen').style.display = 'none';
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
                        panelName: data.panelName || 'Official Admin Panel',
                        role: data.adminRole || 'Super Admin'
                    };
                    document.getElementById('sidebarPanelTitle').innerText = (data.panelName || 'OFFICIAL ADMIN PANEL').toUpperCase();
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
            const usernameOrId = document.getElementById('loginUsernameOrId').value.trim();
            const password = document.getElementById('loginPassword').value.trim();
            const alertBox = document.getElementById('loginAlertBox');
            const btn = document.getElementById('loginBtn');

            if (!usernameOrId || !password) {
                alertBox.innerText = 'Invalid Username or Password';
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
                    body: JSON.stringify({ usernameOrId: usernameOrId, password: password })
                });
                const data = await res.json();
                if (data.success && data.session) {
                    currentAdminSession = data.session;
                    sessionStorage.setItem('adminToken', currentAdminSession.token);
                    document.getElementById('sidebarPanelTitle').innerText = (currentAdminSession.panelName || 'OFFICIAL ADMIN PANEL').toUpperCase();
                    document.getElementById('sidebarAdminName').innerText = currentAdminSession.username;
                    document.getElementById('sidebarAdminId').innerText = currentAdminSession.userId;
                    showToast('Welcome to Official Admin Panel!', 'success');
                    showAdminDashboard();
                } else {
                    alertBox.innerText = data.message || 'Invalid Username or Password';
                    alertBox.style.display = 'block';
                    showToast(data.message || 'Invalid Username or Password', 'error');
                }
            } catch (err) {
                alertBox.innerText = 'Network error connecting to Admin Server.';
                alertBox.style.display = 'block';
                showToast('Connection failed', 'error');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>🔐 Sign In</span>';
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
                link: ['🔗 LINK Management', 'Users registered under your unique admin link & their assigned work'],
                frames: ['👑 Frame Management', 'Exclusive Official 1 Frame Distribution System & Audit History']
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
            if (tabId === 'frames') loadFrameHistory();
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

        // ==========================================
        // OFFICIAL 1 FRAME MANAGEMENT LOGIC
        // ==========================================
        const OFFICIAL_FRAME_INFO = {
            'frame_official': { title: 'Official', badge: 'OFFICIAL', icon: '🛡️', color: '#FFD700', desc: 'Authoritative Official Staff Identity Frame' },
            'frame_manager': { title: 'Manager', badge: 'MANAGER', icon: '👑', color: '#BA68C8', desc: 'Platform Operations & Managerial Authority Frame' },
            'frame_super_admin': { title: 'Super Admin', badge: 'SUPER ADMIN', icon: '🔥', color: '#FF5252', desc: 'Supreme Administrative Authority & Executive Crest' },
            'frame_admin': { title: 'Admin', badge: 'ADMIN', icon: '⚔️', color: '#00E5FF', desc: 'Official Platform Administrator Crest' },
            'frame_admin_leader': { title: 'Admin Leader', badge: 'ADMIN LEADER', icon: '🌟', color: '#FFB300', desc: 'Administrative Command Leadership Laurel' },
            'frame_bd': { title: 'BD', badge: 'BD', icon: '🐉', color: '#00E676', desc: 'Business Development Officer Official Frame' },
            'frame_bd_leader': { title: 'BD Leader', badge: 'BD LEADER', icon: '🏆', color: '#69F0AE', desc: 'Director of Business Development Frame' },
            'frame_agency': { title: 'Agency', badge: 'AGENCY', icon: '🏢', color: '#7C4DFF', desc: 'Official Certified Host Talent Agency Frame' },
            'frame_agency_leader': { title: 'Agency Leader', badge: 'AGENCY LEADER', icon: '💎', color: '#B388FF', desc: 'Agency Guild Master & Managing Director Frame' },
            'frame_host': { title: 'Host', badge: 'HOST', icon: '🎙️', color: '#FF4081', desc: 'Certified Star Audio Room Host Frame' },
            'frame_coin_reseller': { title: 'Coin Reseller', badge: 'COIN RESELLER', icon: '🪙', color: '#FFD54F', desc: 'Authorized Official Coin Reseller Merchant Frame' },
            'frame_super_coin_reseller': { title: 'Super Coin Reseller', badge: 'SUPER COIN RESELLER', icon: '💰', color: '#FFE082', desc: 'Premier Super Coin Merchant Master Frame' },
            'frame_cs': { title: 'C\'s', badge: 'C\'S', icon: '⚡', color: '#40C4FF', desc: 'Customer Support Official Representative Frame' },
            'frame_cs_leader': { title: 'C\'s Leader', badge: 'C\'S LEADER', icon: '💠', color: '#82B1FF', desc: 'Head of Customer Services & Support Operations Frame' }
        };

        function onOfficialFrameChanged() {
            const frameId = document.getElementById('selectFrameInput').value;
            const meta = OFFICIAL_FRAME_INFO[frameId] || { title: 'Select a Frame', badge: 'OFFICIAL', icon: '🛡️', color: '#FFD700', desc: 'Important official frames only' };

            const orb = document.getElementById('previewBadgeOrb');
            const titleEl = document.getElementById('previewFrameTitle');
            const badgeEl = document.getElementById('previewBadgeTag');
            const descEl = document.getElementById('previewFrameDescription');

            if (orb) {
                orb.innerText = meta.icon;
                orb.style.borderColor = meta.color;
                orb.style.background = `radial-gradient(circle, ${'$'}{meta.color}33 0%, rgba(0,0,0,0.7) 100%)`;
                orb.style.boxShadow = `0 0 14px ${'$'}{meta.color}55`;
            }
            if (titleEl) titleEl.innerText = meta.title;
            if (badgeEl) {
                badgeEl.innerText = meta.badge;
                badgeEl.style.color = meta.color;
                badgeEl.style.borderColor = meta.color;
                badgeEl.style.background = `${'$'}{meta.color}22`;
            }
            if (descEl) descEl.innerText = meta.desc;
        }

        let frameConflictTimeout = null;
        function checkFrameConflictDebounced() {
            clearTimeout(frameConflictTimeout);
            frameConflictTimeout = setTimeout(checkFrameConflict, 350);
        }

        async function checkFrameConflict() {
            const userId = document.getElementById('targetUserIdInput')?.value?.trim() || '';
            const noticeEl = document.getElementById('frameConflictNotice');
            const sendBtn = document.getElementById('btnSendOfficialFrame');
            if (!userId) {
                if (noticeEl) noticeEl.style.display = 'none';
                if (sendBtn) sendBtn.disabled = false;
                return;
            }

            try {
                const res = await fetch('/api/admin/frames/check-conflict?userId=' + encodeURIComponent(userId), { headers: getAuthHeaders() });
                const data = await res.json();
                if (data.hasConflict) {
                    if (noticeEl) {
                        noticeEl.style.display = 'block';
                        noticeEl.style.background = 'rgba(255, 42, 133, 0.15)';
                        noticeEl.style.border = '1px solid #FF2A85';
                        noticeEl.style.color = '#FF80AB';
                        noticeEl.innerHTML = `⚠️ <strong>Conflict Notice:</strong> ${'$'}{data.message}. A user can only equip one active official frame at a time.`;
                    }
                } else {
                    if (noticeEl) {
                        noticeEl.style.display = 'block';
                        noticeEl.style.background = 'rgba(0, 230, 118, 0.1)';
                        noticeEl.style.border = '1px solid #00E676';
                        noticeEl.style.color = '#B9F6CA';
                        noticeEl.innerHTML = `✓ User ID verified. No conflicting active official frame found.`;
                    }
                }
            } catch (err) {
                console.error(err);
            }
        }

        async function handleSendOfficialFrame(e) {
            e.preventDefault();
            const frameId = document.getElementById('selectFrameInput').value;
            const userId = document.getElementById('targetUserIdInput').value.trim();
            const days = parseInt(document.getElementById('selectDaysInput').value, 10);
            const btn = document.getElementById('btnSendOfficialFrame');

            if (!frameId) {
                showToast('Please select an official frame', 'error');
                return;
            }
            if (!userId) {
                showToast('Please enter target User ID number', 'error');
                return;
            }
            if (!days || days <= 0) {
                showToast('Please select validity days', 'error');
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span>⏳ Sending Frame...</span>';

            try {
                const res = await fetch('/api/admin/frames/send', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ frameId, userId, days })
                });
                const data = await res.json();
                if (data.success) {
                    showToast(data.message || 'Official frame sent successfully!', 'success');
                    document.getElementById('targetUserIdInput').value = '';
                    const noticeEl = document.getElementById('frameConflictNotice');
                    if (noticeEl) noticeEl.style.display = 'none';
                    loadFrameHistory();
                } else {
                    showToast(data.message || 'Failed to send frame', 'error');
                }
            } catch (err) {
                showToast('Network error sending official frame', 'error');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>👑 SEND FRAME</span>';
            }
        }

        let frameHistoryTimeout = null;
        function debounceFrameHistorySearch() {
            clearTimeout(frameHistoryTimeout);
            frameHistoryTimeout = setTimeout(loadFrameHistory, 300);
        }

        async function loadFrameHistory() {
            const tableBody = document.getElementById('frameHistoryTableBody');
            if (!tableBody) return;
            const query = document.getElementById('frameHistorySearch')?.value?.trim() || '';

            try {
                const res = await fetch('/api/admin/frames/history?query=' + encodeURIComponent(query), { headers: getAuthHeaders() });
                const list = await res.json();

                if (!list || list.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 24px;">No official frame assignments found.</td></tr>';
                    return;
                }

                tableBody.innerHTML = list.map(item => {
                    const meta = OFFICIAL_FRAME_INFO[item.frameId] || { icon: '🛡️', color: '#FFD700' };
                    let statusBadge = '';
                    let actionBtn = '';

                    if (item.status === 'Active') {
                        statusBadge = `<span class="badge-role" style="background: rgba(0,230,118,0.15); color: #00E676; border: 1px solid #00E676;">🟢 Active (${'$'}{item.remainingDays}d left)</span>`;
                        actionBtn = `<button class="btn-danger" style="padding: 4px 10px; font-size: 11px;" onclick="handleRevokeFrame('${'$'}{item.id}', '${'$'}{item.userId}', '${'$'}{item.frameName}')">Revoke</button>`;
                    } else if (item.status === 'Expired') {
                        statusBadge = `<span class="badge-role" style="background: rgba(255,255,255,0.08); color: var(--text-muted); border: 1px solid var(--border-color);">⏳ Expired</span>`;
                        actionBtn = `<span style="font-size: 11px; color: var(--text-muted);">Auto-Removed</span>`;
                    } else {
                        statusBadge = `<span class="badge-role" style="background: rgba(255,42,133,0.15); color: #FF2A85; border: 1px solid #FF2A85;">🚫 Revoked</span>`;
                        actionBtn = `<span style="font-size: 11px; color: var(--text-muted);">Revoked</span>`;
                    }

                    return `
                        <tr>
                            <td>
                                <span style="font-family: 'JetBrains Mono', monospace; font-weight: 700; color: var(--cyan); background: rgba(0,229,255,0.1); padding: 3px 8px; border-radius: 4px; border: 1px solid rgba(0,229,255,0.2);">
                                    ${'$'}{item.userId}
                                </span>
                            </td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 8px;">
                                    <span style="font-size: 16px;">${'$'}{meta.icon}</span>
                                    <strong style="color: #FFF;">${'$'}{item.frameName}</strong>
                                </div>
                            </td>
                            <td>
                                <span style="font-weight: 700; color: var(--gold);">${'$'}{item.days} Days</span>
                            </td>
                            <td>
                                <span style="font-size: 12px; color: var(--text-secondary);">${'$'}{item.sendDateFormatted || item.assignedAt}</span>
                            </td>
                            <td>
                                <span style="font-size: 12px; color: var(--text-secondary);">${'$'}{item.expiryDateFormatted || item.expiresAt}</span>
                            </td>
                            <td>${'$'}{statusBadge}</td>
                            <td>${'$'}{actionBtn}</td>
                        </tr>
                    `;
                }).join('');
            } catch (err) {
                console.error(err);
                tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--danger); padding: 20px;">Failed to load frame history.</td></tr>';
            }
        }

        async function handleRevokeFrame(assignmentId, userId, frameName) {
            if (!confirm(`Are you sure you want to revoke official frame "${'$'}{frameName}" from User ID ${'$'}{userId}? The frame will be immediately removed from the user's account.`)) {
                return;
            }

            try {
                const res = await fetch('/api/admin/frames/revoke', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ assignmentId })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Official frame revoked and removed from user', 'success');
                    loadFrameHistory();
                } else {
                    showToast(data.message || 'Failed to revoke frame', 'error');
                }
            } catch (err) {
                showToast('Network error revoking frame', 'error');
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
