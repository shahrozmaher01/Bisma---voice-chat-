package com.example.data.admin

object AdminWebPageTemplate {

    fun getHtml(): String {
        return (PART_CSS + PART_LAYOUT + PART_JS_1 + PART_JS_2).replace("@@", "$")
    }

    private const val PART_CSS = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Bisma Live - Super Admin Control Center</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-base: #0B0E14;
            --bg-surface: #121824;
            --bg-card: #182032;
            --bg-card-hover: #202B42;
            --border-color: rgba(255, 255, 255, 0.08);
            --border-accent: rgba(255, 42, 133, 0.3);
            --primary: #FF2A85;
            --primary-glow: rgba(255, 42, 133, 0.25);
            --gold: #FFD700;
            --cyan: #00E5FF;
            --emerald: #00E676;
            --danger: #FF1744;
            --warning: #FF9100;
            --text-primary: #FFFFFF;
            --text-secondary: #94A3B8;
            --text-muted: #64748B;
            --radius-sm: 8px;
            --radius-md: 14px;
            --radius-lg: 20px;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Plus Jakarta Sans', sans-serif;
            -webkit-tap-highlight-color: transparent;
        }

        body {
            background-color: var(--bg-base);
            color: var(--text-primary);
            min-height: 100vh;
            overflow-x: hidden;
            display: flex;
            flex-direction: column;
        }

        .glass-panel {
            background: var(--bg-surface);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            backdrop-filter: blur(12px);
        }

        .glass-card {
            background: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            transition: all 0.2s ease;
        }

        .glass-card:hover {
            border-color: var(--border-accent);
            box-shadow: 0 8px 24px rgba(0,0,0,0.4);
        }

        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: var(--bg-base); }
        ::-webkit-scrollbar-thumb { background: #2A364F; border-radius: 4px; }

        #loginScreen {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 20px;
            background: radial-gradient(circle at top, #1E1538 0%, var(--bg-base) 70%);
        }

        .login-box {
            width: 100%;
            max-width: 440px;
            padding: 36px 30px;
            background: var(--bg-surface);
            border: 1px solid rgba(255, 42, 133, 0.2);
            border-radius: var(--radius-lg);
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.6);
            text-align: center;
        }

        .brand-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 6px 14px;
            background: rgba(255, 42, 133, 0.12);
            border: 1px solid var(--border-accent);
            border-radius: 999px;
            color: var(--primary);
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 0.5px;
            text-transform: uppercase;
            margin-bottom: 20px;
        }

        .form-group { text-align: left; margin-bottom: 18px; }
        .form-label { display: block; font-size: 13px; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; }
        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 12px 14px;
            background: #0E131F;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            color: #FFFFFF;
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s;
        }
        .form-input:focus, .form-select:focus, .form-textarea:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 2px var(--primary-glow);
        }

        .btn-primary {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            width: 100%;
            padding: 13px 20px;
            background: linear-gradient(135deg, #FF2A85 0%, #FF5252 100%);
            color: white;
            border: none;
            border-radius: var(--radius-sm);
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(255, 42, 133, 0.35);
            transition: all 0.2s ease;
        }
        .btn-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(255, 42, 133, 0.5); }

        .btn-secondary {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 9px 16px;
            background: var(--bg-card);
            color: var(--text-primary);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        .btn-secondary:hover { background: var(--bg-card-hover); border-color: var(--primary); }

        .btn-danger { background: rgba(255, 23, 68, 0.15); color: var(--danger); border: 1px solid rgba(255, 23, 68, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        .btn-danger:hover { background: rgba(255, 23, 68, 0.3); }

        .btn-success { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.3); padding: 8px 14px; border-radius: var(--radius-sm); font-size: 12px; font-weight: 700; cursor: pointer; }
        .btn-success:hover { background: rgba(0, 230, 118, 0.3); }

        #adminApp { display: none; min-height: 100vh; }
        .sidebar { width: 260px; background: #0E131F; border-right: 1px solid var(--border-color); display: flex; flex-direction: column; position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
        .sidebar-brand { padding: 24px 20px; display: flex; align-items: center; gap: 12px; border-bottom: 1px solid var(--border-color); }
        .sidebar-brand-logo { width: 38px; height: 38px; border-radius: 10px; background: linear-gradient(135deg, #FF2A85, #FFD700); display: flex; align-items: center; justify-content: center; font-size: 20px; }
        .sidebar-menu { flex: 1; padding: 16px 12px; overflow-y: auto; display: flex; flex-direction: column; gap: 4px; }
        .menu-category { font-size: 11px; font-weight: 800; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.8px; padding: 12px 12px 6px; }
        .menu-item { display: flex; align-items: center; gap: 12px; padding: 11px 14px; border-radius: var(--radius-sm); color: var(--text-secondary); font-size: 13.5px; font-weight: 600; cursor: pointer; transition: all 0.15s ease; text-decoration: none; }
        .menu-item:hover { background: var(--bg-card); color: var(--text-primary); }
        .menu-item.active { background: rgba(255, 42, 133, 0.15); color: #FFFFFF; border: 1px solid var(--border-accent); font-weight: 700; }
        .menu-item-icon { font-size: 18px; width: 24px; text-align: center; }
        .sidebar-footer { padding: 16px; border-top: 1px solid var(--border-color); background: rgba(0,0,0,0.2); }
        .admin-profile-card { display: flex; align-items: center; gap: 10px; }

        .main-wrapper { flex: 1; margin-left: 260px; min-height: 100vh; display: flex; flex-direction: column; background: var(--bg-base); }
        .topbar { height: 68px; background: rgba(14, 19, 31, 0.8); backdrop-filter: blur(10px); border-bottom: 1px solid var(--border-color); padding: 0 24px; display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 90; }
        .mobile-menu-btn { display: none; background: none; border: none; color: white; font-size: 24px; cursor: pointer; }
        .topbar-title-wrap h2 { font-size: 18px; font-weight: 800; color: #FFFFFF; }
        .topbar-title-wrap p { font-size: 12px; color: var(--text-muted); }
        .topbar-actions { display: flex; align-items: center; gap: 12px; }

        .badge-role { padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; }
        .role-super-admin { background: rgba(255, 215, 0, 0.15); color: var(--gold); border: 1px solid rgba(255, 215, 0, 0.4); }
        .role-admin { background: rgba(255, 42, 133, 0.15); color: var(--primary); border: 1px solid rgba(255, 42, 133, 0.4); }
        .role-manager { background: rgba(0, 229, 255, 0.15); color: var(--cyan); border: 1px solid rgba(0, 229, 255, 0.4); }
        .role-bd { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.4); }
        .role-agency { background: rgba(168, 85, 247, 0.15); color: #C084FC; border: 1px solid rgba(168, 85, 247, 0.4); }
        .role-reseller { background: rgba(255, 145, 0, 0.15); color: var(--warning); border: 1px solid rgba(255, 145, 0, 0.4); }
        .role-user { background: rgba(148, 163, 184, 0.15); color: var(--text-secondary); border: 1px solid var(--border-color); }

        .content-area { padding: 24px; flex: 1; }
        .tab-content { display: none; }
        .tab-content.active { display: block; animation: fadeIn 0.25s ease; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }

        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .stat-card { padding: 20px; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); position: relative; overflow: hidden; }
        .stat-card-title { font-size: 13px; font-weight: 600; color: var(--text-muted); margin-bottom: 8px; }
        .stat-card-value { font-size: 26px; font-weight: 800; color: #FFFFFF; }
        .stat-card-icon { position: absolute; right: 18px; top: 18px; font-size: 24px; opacity: 0.7; }

        .table-responsive { width: 100%; overflow-x: auto; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); margin-top: 16px; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { padding: 14px 16px; font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.6px; border-bottom: 1px solid var(--border-color); background: rgba(0,0,0,0.2); }
        td { padding: 14px 16px; font-size: 13.5px; border-bottom: 1px solid var(--border-color); color: var(--text-secondary); }
        tr:hover td { background: rgba(255, 255, 255, 0.02); color: var(--text-primary); }

        .avatar-sm { width: 34px; height: 34px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border-color); }
        .user-cell { display: flex; align-items: center; gap: 10px; font-weight: 600; color: #FFFFFF; }

        .modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.75); backdrop-filter: blur(6px); display: none; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
        .modal-box { width: 100%; max-width: 520px; background: var(--bg-surface); border: 1px solid var(--border-accent); border-radius: var(--radius-lg); padding: 28px; box-shadow: 0 25px 60px rgba(0,0,0,0.8); max-height: 90vh; overflow-y: auto; }
        .modal-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; border-bottom: 1px solid var(--border-color); padding-bottom: 12px; }
        .modal-header h3 { font-size: 18px; font-weight: 800; }
        .close-btn { background: none; border: none; color: var(--text-muted); font-size: 22px; cursor: pointer; }
        .close-btn:hover { color: white; }

        .toast-container { position: fixed; bottom: 24px; right: 24px; z-index: 2000; display: flex; flex-direction: column; gap: 10px; }
        .toast { padding: 12px 20px; border-radius: var(--radius-sm); font-size: 13px; font-weight: 600; box-shadow: 0 10px 30px rgba(0,0,0,0.5); display: flex; align-items: center; gap: 8px; animation: slideIn 0.3s ease; }
        .toast-success { background: #00E676; color: #003314; }
        .toast-error { background: #FF1744; color: white; }
        @keyframes slideIn { from { transform: translateX(100%); } to { transform: translateX(0); } }

        @media (max-width: 900px) {
            .sidebar { transform: translateX(-100%); }
            .sidebar.open { transform: translateX(0); }
            .main-wrapper { margin-left: 0; }
            .mobile-menu-btn { display: block; }
        }
    </style>
</head>
"""

    private const val PART_LAYOUT = """<body>
    <!-- Login Screen -->
    <div id="loginScreen">
        <div class="login-box glass-panel">
            <div class="brand-badge">🛡️ Super Admin Control</div>
            <h1 style="font-size: 24px; font-weight: 800; margin-bottom: 6px; color: #FFFFFF;">Bisma Live</h1>
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 26px;">Central Administrative Management Console</p>

            <form id="loginForm" onsubmit="handleLoginSubmit(event)">
                <div class="form-group">
                    <label class="form-label">User ID / Administrative ID</label>
                    <input type="text" id="loginUserId" class="form-input" placeholder="e.g. 777888 or your authorized User ID" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Password</label>
                    <input type="password" id="loginPassword" class="form-input" placeholder="Enter secure password" required>
                </div>
                <button type="submit" id="loginBtn" class="btn-primary" style="margin-top: 10px;">
                    <span>Authenticate & Access Console</span>
                </button>
            </form>
            <p style="margin-top: 20px; font-size: 12px; color: var(--text-muted);">
                🔒 Protected by End-to-End Server-Side Role Authorization.
            </p>
        </div>
    </div>

    <!-- Admin App Wrapper -->
    <div id="adminApp">
        <!-- Sidebar Navigation -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-logo">💎</div>
                <div>
                    <h3 style="font-size: 15px; font-weight: 800; color: #FFFFFF;">Bisma Admin</h3>
                    <p style="font-size: 11px; color: var(--primary); font-weight: 700;">Super Control Center</p>
                </div>
            </div>

            <div class="sidebar-menu">
                <div class="menu-category">Overview</div>
                <a class="menu-item active" onclick="switchTab('overview')">
                    <span class="menu-item-icon">📊</span>
                    <span>Dashboard Stats</span>
                </a>

                <div class="menu-category">Governance & Roles</div>
                <a class="menu-item" onclick="switchTab('users')">
                    <span class="menu-item-icon">👥</span>
                    <span>User Directory</span>
                </a>
                <a class="menu-item" onclick="switchTab('roles')">
                    <span class="menu-item-icon">👑</span>
                    <span>Role Management</span>
                </a>
                <a class="menu-item" onclick="switchTab('rooms')">
                    <span class="menu-item-icon">🎙️</span>
                    <span>Voice Rooms</span>
                </a>

                <div class="menu-category">Economy & Business</div>
                <a class="menu-item" onclick="switchTab('resellers')">
                    <span class="menu-item-icon">🪙</span>
                    <span>Coin Resellers & Balances</span>
                </a>
                <a class="menu-item" onclick="switchTab('agencies')">
                    <span class="menu-item-icon">🏢</span>
                    <span>Agencies & BD</span>
                </a>

                <div class="menu-category">System & Customization</div>
                <a class="menu-item" onclick="switchTab('app_control')">
                    <span class="menu-item-icon">⚙️</span>
                    <span>App Control Center</span>
                </a>
                <a class="menu-item" onclick="switchTab('reports')">
                    <span class="menu-item-icon">🚨</span>
                    <span>Reports & Moderation</span>
                </a>
                <a class="menu-item" onclick="switchTab('audit_logs')">
                    <span class="menu-item-icon">📜</span>
                    <span>Audit Trail Logs</span>
                </a>
            </div>

            <div class="sidebar-footer">
                <div class="admin-profile-card">
                    <img id="adminAvatarImg" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100" class="avatar-sm" alt="Admin Avatar">
                    <div style="flex: 1; min-width: 0;">
                        <div id="adminNameTxt" style="font-size: 13px; font-weight: 700; color: #FFFFFF; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Admin</div>
                        <div id="adminRoleBadge" class="badge-role role-super-admin" style="font-size: 10px; display: inline-block; padding: 2px 6px; margin-top: 2px;">SUPER ADMIN</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Wrapper -->
        <div class="main-wrapper">
            <div class="topbar">
                <div style="display: flex; align-items: center; gap: 16px;">
                    <button class="mobile-menu-btn" onclick="toggleSidebar()">☰</button>
                    <div class="topbar-title-wrap">
                        <h2 id="currentTabTitle">Dashboard Overview</h2>
                        <p id="currentTabSubtitle">Real-time system telemetry and live application controls</p>
                    </div>
                </div>

                <div class="topbar-actions">
                    <button class="btn-secondary" onclick="refreshCurrentTab()" style="padding: 7px 12px;">
                        <span>🔄 Refresh</span>
                    </button>
                    <button class="btn-danger" onclick="handleLogout()" style="padding: 7px 12px;">
                        <span>🚪 Exit Console</span>
                    </button>
                </div>
            </div>

            <div class="content-area">
                <!-- 1. OVERVIEW TAB -->
                <div id="tab_overview" class="tab-content active">
                    <div class="stats-grid" id="dashboardStatsGrid">
                        <!-- Populated by JS -->
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 20px;">
                        <div class="glass-card" style="padding: 20px;">
                            <h3 style="font-size: 15px; font-weight: 800; margin-bottom: 12px;">⚡ Quick Management Actions</h3>
                            <div style="display: flex; flex-direction: column; gap: 10px;">
                                <button class="btn-secondary" onclick="switchTab('users')" style="justify-content: flex-start;">👤 Search & Manage Users</button>
                                <button class="btn-secondary" onclick="switchTab('roles')" style="justify-content: flex-start;">👑 Assign Administrative Roles</button>
                                <button class="btn-secondary" onclick="switchTab('resellers')" style="justify-content: flex-start;">🪙 Adjust Coin / Diamond Balances</button>
                                <button class="btn-secondary" onclick="switchTab('app_control')" style="justify-content: flex-start;">🎨 Configure Branding & App Controls</button>
                            </div>
                        </div>

                        <div class="glass-card" style="padding: 20px;">
                            <h3 style="font-size: 15px; font-weight: 800; margin-bottom: 12px;">🛡️ Real-Time Governance Status</h3>
                            <div style="font-size: 13px; color: var(--text-secondary); line-height: 1.6;">
                                <p>• <strong>Privilege Protection:</strong> Active (Strict Server Authorization)</p>
                                <p>• <strong>Audit Logging:</strong> All state-changing actions tracked</p>
                                <p>• <strong>Data Layer:</strong> Real-time Room Database synchronization</p>
                                <p>• <strong>Active Channel:</strong> Bisma Live Local Web Engine</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 2. USERS TAB -->
                <div id="tab_users" class="tab-content">
                    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 16px; flex-wrap: wrap;">
                        <input type="text" id="userSearchInput" class="form-input" style="max-width: 320px;" placeholder="Search User ID or username...">
                        <button class="btn-secondary" onclick="loadUsers()">🔍 Search</button>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>User ID</th>
                                    <th>Role</th>
                                    <th>Level</th>
                                    <th>Coins / Diamonds</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="usersTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 3. ROLES TAB -->
                <div id="tab_roles" class="tab-content">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <h3 style="font-size: 16px; font-weight: 800;">Authorized Role Assignments</h3>
                        <button class="btn-primary" onclick="openAssignRoleModal()" style="width: auto;">+ Assign New Role</button>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>User ID</th>
                                    <th>Username</th>
                                    <th>Assigned Role</th>
                                    <th>Permissions</th>
                                    <th>Assigned By</th>
                                    <th>Assigned At</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="rolesTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 4. ROOMS TAB -->
                <div id="tab_rooms" class="tab-content">
                    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 16px;">
                        <input type="text" id="roomSearchInput" class="form-input" style="max-width: 320px;" placeholder="Search Room ID or title...">
                        <button class="btn-secondary" onclick="loadRooms()">🔍 Search</button>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Room</th>
                                    <th>Room ID</th>
                                    <th>Host / Owner</th>
                                    <th>Seats</th>
                                    <th>Online</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="roomsTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 5. RESELLERS & COINS TAB -->
                <div id="tab_resellers" class="tab-content">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <h3 style="font-size: 16px; font-weight: 800;">Coin Resellers & Currency Adjustments</h3>
                        <button class="btn-primary" onclick="openBalanceModal()" style="width: auto;">💰 Authorized Balance Adjustment</button>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Reseller / Target User</th>
                                    <th>User ID</th>
                                    <th>Coins</th>
                                    <th>Diamonds</th>
                                    <th>Role</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="resellersTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 6. AGENCIES & BD TAB -->
                <div id="tab_agencies" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Agency Name</th>
                                    <th>Agency ID</th>
                                    <th>Owner</th>
                                    <th>Members</th>
                                    <th>Level</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="agenciesTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 7. APP CONTROL CENTER -->
                <div id="tab_app_control" class="tab-content">
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px;">
                        <!-- General & Branding -->
                        <div class="glass-card" style="padding: 24px;">
                            <h3 style="font-size: 16px; font-weight: 800; margin-bottom: 16px; color: var(--primary);">🎨 App Branding & Names</h3>
                            <div class="form-group">
                                <label class="form-label">Application Name</label>
                                <input type="text" id="cfg_app_name" class="form-input" value="Bisma Live">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Welcome Message</label>
                                <input type="text" id="cfg_welcome_message" class="form-input" value="Welcome to Bisma Voice Chat! ✨">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Announcement Banner</label>
                                <textarea id="cfg_announcement_banner" class="form-textarea" rows="2">📢 Official Notice: Welcome to Bisma Live Community!</textarea>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Support Email</label>
                                <input type="text" id="cfg_support_email" class="form-input" value="support@bismalive.com">
                            </div>
                            <button class="btn-primary" onclick="saveGeneralConfigs()">💾 Save General Configs</button>
                        </div>

                        <!-- Feature Switches -->
                        <div class="glass-card" style="padding: 24px;">
                            <h3 style="font-size: 16px; font-weight: 800; margin-bottom: 16px; color: var(--gold);">⚡ Feature Switches</h3>
                            <div style="display: flex; flex-direction: column; gap: 14px;">
                                <label style="display: flex; align-items: center; justify-content: space-between; font-size: 13.5px;">
                                    <span>Voice Rooms Feature</span>
                                    <input type="checkbox" id="cfg_feature_voice_rooms" checked>
                                </label>
                                <label style="display: flex; align-items: center; justify-content: space-between; font-size: 13.5px;">
                                    <span>Live In-Room Chat</span>
                                    <input type="checkbox" id="cfg_feature_live_chat" checked>
                                </label>
                                <label style="display: flex; align-items: center; justify-content: space-between; font-size: 13.5px;">
                                    <span>Moments Community Feed</span>
                                    <input type="checkbox" id="cfg_feature_moments" checked>
                                </label>
                                <label style="display: flex; align-items: center; justify-content: space-between; font-size: 13.5px;">
                                    <span>Coin Reseller Portal</span>
                                    <input type="checkbox" id="cfg_feature_coin_reseller" checked>
                                </label>
                                <label style="display: flex; align-items: center; justify-content: space-between; font-size: 13.5px; color: var(--danger);">
                                    <span>Maintenance Mode</span>
                                    <input type="checkbox" id="cfg_feature_maintenance_mode">
                                </label>
                            </div>
                            <button class="btn-primary" onclick="saveFeatureSwitches()" style="margin-top: 24px;">💾 Update Feature Switches</button>
                        </div>
                    </div>
                </div>

                <!-- 8. REPORTS TAB -->
                <div id="tab_reports" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Target</th>
                                    <th>Type</th>
                                    <th>Reporter</th>
                                    <th>Reason</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="reportsTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 9. AUDIT LOGS TAB -->
                <div id="tab_audit_logs" class="tab-content">
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Timestamp</th>
                                    <th>Admin</th>
                                    <th>Role</th>
                                    <th>Action</th>
                                    <th>Target</th>
                                    <th>Previous / New Value</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody id="auditLogsTableBody">
                                <!-- Populated by JS -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modals -->
    <!-- Role Assignment Modal -->
    <div id="roleModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Assign Administrative Role</h3>
                <button class="close-btn" onclick="closeModal('roleModal')">✕</button>
            </div>
            <div class="form-group">
                <label class="form-label">Target User ID</label>
                <input type="text" id="roleModalUserId" class="form-input" placeholder="e.g. 102938" required>
            </div>
            <div class="form-group">
                <label class="form-label">Role</label>
                <select id="roleModalRoleSelect" class="form-select">
                    <option value="Admin">Admin</option>
                    <option value="Manager">Manager</option>
                    <option value="BD">BD</option>
                    <option value="Agency">Agency</option>
                    <option value="Coin Reseller">Coin Reseller</option>
                    <option value="Super Admin">Super Admin</option>
                    <option value="User">User (Revoke Privileges)</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Custom Permissions (Optional Comma-separated)</label>
                <input type="text" id="roleModalPermissions" class="form-input" placeholder="e.g. ROOM_MODERATE, USER_VIEW">
            </div>
            <button class="btn-primary" onclick="submitRoleAssignment()">Save Role Assignment</button>
        </div>
    </div>

    <!-- Balance Adjustment Modal -->
    <div id="balanceModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Coin / Diamond Balance Adjustment</h3>
                <button class="close-btn" onclick="closeModal('balanceModal')">✕</button>
            </div>
            <div class="form-group">
                <label class="form-label">Target User ID</label>
                <input type="text" id="balModalUserId" class="form-input" placeholder="e.g. 102938" required>
            </div>
            <div class="form-group">
                <label class="form-label">Currency Type</label>
                <select id="balModalType" class="form-select">
                    <option value="coins">Coins</option>
                    <option value="diamonds">Diamonds</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Amount (Positive to add, Negative to deduct)</label>
                <input type="number" id="balModalAmount" class="form-input" placeholder="e.g. 5000" required>
            </div>
            <div class="form-group">
                <label class="form-label">Audit Reason (Required)</label>
                <input type="text" id="balModalReason" class="form-input" placeholder="e.g. Official Reseller Top-up" required>
            </div>
            <button class="btn-primary" onclick="submitBalanceAdjustment()">Execute Balance Change</button>
        </div>
    </div>

    <!-- Toast Notifications -->
    <div class="toast-container" id="toastContainer"></div>
"""

    private const val PART_JS_1 = """    <script>
        let currentSession = null;
        let currentTab = 'overview';

        async function apiCall(endpoint, method = 'GET', body = null) {
            const headers = { 'Content-Type': 'application/json' };
            if (currentSession && currentSession.token) {
                headers['Authorization'] = 'Bearer ' + currentSession.token;
            }
            const opts = { method, headers };
            if (body) opts.body = JSON.stringify(body);

            try {
                const res = await fetch(endpoint, opts);
                if (res.status === 401) {
                    showToast('Session expired or unauthorized. Please re-login.', 'error');
                    handleLogout();
                    return { success: false, message: 'Unauthorized' };
                }
                return await res.json();
            } catch (e) {
                return { success: false, message: e.message };
            }
        }

        function showToast(msg, type = 'success') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.innerHTML = (type === 'success' ? '✅ ' : '⚠️ ') + msg;
            container.appendChild(toast);
            setTimeout(() => { toast.remove(); }, 3500);
        }

        async function handleLoginSubmit(e) {
            e.preventDefault();
            const userId = document.getElementById('loginUserId').value.trim();
            const password = document.getElementById('loginPassword').value;

            const btn = document.getElementById('loginBtn');
            btn.innerHTML = '<span>Verifying credentials...</span>';
            btn.disabled = true;

            const res = await apiCall('/api/admin/login', 'POST', { userId, password });
            btn.innerHTML = '<span>Authenticate & Access Console</span>';
            btn.disabled = false;

            if (res.success && res.session) {
                currentSession = res.session;
                document.getElementById('loginScreen').style.display = 'none';
                document.getElementById('adminApp').style.display = 'flex';
                document.getElementById('adminNameTxt').textContent = currentSession.username;
                document.getElementById('adminRoleBadge').textContent = currentSession.role;
                document.getElementById('adminRoleBadge').className = 'badge-role role-' + currentSession.role.toLowerCase().replace(/ /g, '-');
                showToast('Welcome, ' + currentSession.username + ' (' + currentSession.role + ')', 'success');
                loadDashboardStats();
            } else {
                showToast(res.message || 'Authentication failed', 'error');
            }
        }

        function handleLogout() {
            currentSession = null;
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('loginScreen').style.display = 'flex';
        }

        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('open');
        }

        function switchTab(tabId) {
            currentTab = tabId;
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));

            const tabEl = document.getElementById('tab_' + tabId);
            if (tabEl) tabEl.classList.add('active');

            const titles = {
                overview: 'Dashboard Overview',
                users: 'User Directory & Moderation',
                roles: 'Role & Permission Management',
                rooms: 'Voice Rooms Management',
                resellers: 'Coin Resellers & Balances',
                agencies: 'Agencies & BD Directory',
                app_control: 'App Control Center & Branding',
                reports: 'Moderation & Reports',
                audit_logs: 'Audit Trail Records'
            };

            document.getElementById('currentTabTitle').textContent = titles[tabId] || 'Admin Console';
            refreshCurrentTab();
            if (window.innerWidth <= 900) {
                document.getElementById('sidebar').classList.remove('open');
            }
        }

        function refreshCurrentTab() {
            if (currentTab === 'overview') loadDashboardStats();
            else if (currentTab === 'users') loadUsers();
            else if (currentTab === 'roles') loadRoles();
            else if (currentTab === 'rooms') loadRooms();
            else if (currentTab === 'resellers') loadResellers();
            else if (currentTab === 'agencies') loadAgencies();
            else if (currentTab === 'reports') loadReports();
            else if (currentTab === 'audit_logs') loadAuditLogs();
        }

        async function loadDashboardStats() {
            const res = await apiCall('/api/admin/dashboard');
            if (res.success && res.stats) {
                const s = res.stats;
                const grid = document.getElementById('dashboardStatsGrid');
                grid.innerHTML = `
                    <div class="stat-card">
                        <div class="stat-card-title">Total Registered Users</div>
                        <div class="stat-card-value">@@{s.totalUsers}</div>
                        <div class="stat-card-icon">👥</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-card-title">Active Live Rooms</div>
                        <div class="stat-card-value">@@{s.activeRooms}</div>
                        <div class="stat-card-icon">🎙️</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-card-title">Super Admins / Admins</div>
                        <div class="stat-card-value">@@{s.superAdminsCount} / @@{s.adminsCount}</div>
                        <div class="stat-card-icon">👑</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-card-title">Pending Reports</div>
                        <div class="stat-card-value" style="color: @@{s.pendingReports > 0 ? 'var(--danger)' : 'var(--emerald)'}">@@{s.pendingReports}</div>
                        <div class="stat-card-icon">🚨</div>
                    </div>
                `;
            }
        }

        async function loadUsers() {
            const query = document.getElementById('userSearchInput')?.value || '';
            const res = await apiCall('/api/admin/users?query=' + encodeURIComponent(query));
            if (res.success && res.users) {
                const tb = document.getElementById('usersTableBody');
                tb.innerHTML = res.users.map(u => `
                    <tr>
                        <td>
                            <div class="user-cell">
                                <img src="@@{u.avatarUrl}" class="avatar-sm" onerror="this.src='https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100'">
                                <div>
                                    <div>@@{u.username}</div>
                                    <div style="font-size: 11px; color: var(--text-muted);">@@{u.country}</div>
                                </div>
                            </div>
                        </td>
                        <td><code>@@{u.id}</code></td>
                        <td><span class="badge-role role-@@{u.role.toLowerCase().replace(/ /g, '-')}">@@{u.role}</span></td>
                        <td>Lv.@@{u.userLevel} (VIP @@{u.vipLevel})</td>
                        <td>🪙 @@{u.coins.toLocaleString()} / 💎 @@{u.diamonds.toLocaleString()}</td>
                        <td>
                            @@{u.isBanned ? '<span style="color: var(--danger); font-weight:700;">BANNED</span>' : '<span style="color: var(--emerald); font-weight:700;">ACTIVE</span>'}
                        </td>
                        <td>
                            <button class="btn-secondary" onclick="openQuickBalance('@@{u.id}')" style="padding: 4px 8px; font-size: 11px;">🪙 Coins</button>
                            <button class="btn-secondary" onclick="openQuickRole('@@{u.id}', '@@{u.role}')" style="padding: 4px 8px; font-size: 11px;">👑 Role</button>
                            @@{u.isBanned ? 
                                `<button class="btn-success" onclick="toggleBan('@@{u.id}', false)" style="padding: 4px 8px; font-size: 11px;">Unban</button>` : 
                                `<button class="btn-danger" onclick="toggleBan('@@{u.id}', true)" style="padding: 4px 8px; font-size: 11px;">Ban</button>`
                            }
                        </td>
                    </tr>
                `).join('');
            }
        }
"""

    private const val PART_JS_2 = """        async function loadRoles() {
            const res = await apiCall('/api/admin/roles');
            if (res.success && res.roles) {
                const tb = document.getElementById('rolesTableBody');
                tb.innerHTML = res.roles.map(r => `
                    <tr>
                        <td><code>@@{r.userId}</code></td>
                        <td><strong>@@{r.username}</strong></td>
                        <td><span class="badge-role role-@@{r.role.toLowerCase().replace(/ /g, '-')}">@@{r.role}</span></td>
                        <td><small>@@{r.permissions || 'Default Permissions'}</small></td>
                        <td>@@{r.assignedByName}</td>
                        <td>@@{new Date(r.assignedAt).toLocaleDateString()}</td>
                        <td>
                            <button class="btn-danger" onclick="removeRole('@@{r.userId}')" style="padding: 4px 8px; font-size: 11px;">Revoke</button>
                        </td>
                    </tr>
                `).join('');
            }
        }

        async function loadRooms() {
            const query = document.getElementById('roomSearchInput')?.value || '';
            const res = await apiCall('/api/admin/rooms?query=' + encodeURIComponent(query));
            if (res.success && res.rooms) {
                const tb = document.getElementById('roomsTableBody');
                tb.innerHTML = res.rooms.map(r => `
                    <tr>
                        <td>
                            <div class="user-cell">
                                <img src="@@{r.coverUrl}" class="avatar-sm">
                                <div>
                                    <div>@@{r.title}</div>
                                    <div style="font-size: 11px; color: var(--text-muted);">@@{r.country}</div>
                                </div>
                            </div>
                        </td>
                        <td><code>@@{r.id}</code></td>
                        <td>@@{r.ownerName} (ID: @@{r.ownerId})</td>
                        <td>@@{r.seatCount} Seats</td>
                        <td>🟢 @@{r.onlineCount} Online</td>
                        <td>@@{r.isActive ? '<span style="color: var(--emerald);">Active</span>' : '<span style="color: var(--danger);">Closed</span>'}</td>
                        <td>
                            <button class="btn-danger" onclick="closeRoom('@@{r.id}')" style="padding: 4px 8px; font-size: 11px;">End Room</button>
                        </td>
                    </tr>
                `).join('');
            }
        }

        async function loadResellers() {
            const res = await apiCall('/api/admin/users?query=');
            if (res.success && res.users) {
                const tb = document.getElementById('resellersTableBody');
                tb.innerHTML = res.users.map(u => `
                    <tr>
                        <td>
                            <div class="user-cell">
                                <img src="@@{u.avatarUrl}" class="avatar-sm">
                                <strong>@@{u.username}</strong>
                            </div>
                        </td>
                        <td><code>@@{u.id}</code></td>
                        <td>🪙 @@{u.coins.toLocaleString()}</td>
                        <td>💎 @@{u.diamonds.toLocaleString()}</td>
                        <td><span class="badge-role role-@@{u.role.toLowerCase().replace(/ /g, '-')}">@@{u.role}</span></td>
                        <td>
                            <button class="btn-primary" onclick="openQuickBalance('@@{u.id}')" style="width: auto; padding: 4px 10px; font-size: 11px;">🪙 Adjust Balance</button>
                        </td>
                    </tr>
                `).join('');
            }
        }

        async function loadAgencies() {
            const res = await apiCall('/api/admin/agencies');
            if (res.success && res.agencies) {
                const tb = document.getElementById('agenciesTableBody');
                tb.innerHTML = res.agencies.map(a => `
                    <tr>
                        <td><strong>@@{a.name}</strong></td>
                        <td><code>@@{a.id}</code></td>
                        <td>@@{a.ownerName}</td>
                        <td>@@{a.memberCount} Hosts</td>
                        <td>Level @@{a.level}</td>
                        <td>
                            <button class="btn-secondary" style="padding: 4px 8px; font-size: 11px;">View Info</button>
                        </td>
                    </tr>
                `).join('');
            }
        }

        async function loadReports() {
            const res = await apiCall('/api/admin/reports');
            if (res.success && res.reports) {
                const tb = document.getElementById('reportsTableBody');
                tb.innerHTML = res.reports.map(rep => `
                    <tr>
                        <td><strong>@@{rep.targetTitleOrName}</strong></td>
                        <td>@@{rep.targetType}</td>
                        <td>@@{rep.reporterName}</td>
                        <td>@@{rep.reason}</td>
                        <td>@@{new Date(rep.createdAt).toLocaleDateString()}</td>
                        <td><span class="badge-role role-@@{rep.status === 'Resolved' ? 'bd' : 'admin'}">@@{rep.status}</span></td>
                        <td>
                            <button class="btn-success" onclick="resolveReport('@@{rep.id}')" style="padding: 4px 8px; font-size: 11px;">Resolve</button>
                        </td>
                    </tr>
                `).join('');
            }
        }

        async function loadAuditLogs() {
            const res = await apiCall('/api/admin/audit-logs');
            if (res.success && res.logs) {
                const tb = document.getElementById('auditLogsTableBody');
                tb.innerHTML = res.logs.map(l => `
                    <tr>
                        <td><small>@@{new Date(l.timestamp).toLocaleString()}</small></td>
                        <td><strong>@@{l.adminName}</strong> (<code>@@{l.adminId}</code>)</td>
                        <td><span class="badge-role role-@@{l.adminRole.toLowerCase().replace(/ /g, '-')}">@@{l.adminRole}</span></td>
                        <td><code>@@{l.action}</code></td>
                        <td>@@{l.targetName}</td>
                        <td><small>Prev: @@{l.previousValue || 'N/A'} ➔ New: @@{l.newValue || 'N/A'}</small></td>
                        <td>@@{l.isSuccess ? '✅' : '❌'}</td>
                    </tr>
                `).join('');
            }
        }

        function openModal(id) { document.getElementById(id).style.display = 'flex'; }
        function closeModal(id) { document.getElementById(id).style.display = 'none'; }
        function openAssignRoleModal() { openModal('roleModal'); }
        function openBalanceModal() { openModal('balanceModal'); }

        function openQuickRole(userId, currentRole) {
            document.getElementById('roleModalUserId').value = userId;
            document.getElementById('roleModalRoleSelect').value = currentRole;
            openModal('roleModal');
        }

        function openQuickBalance(userId) {
            document.getElementById('balModalUserId').value = userId;
            openModal('balanceModal');
        }

        async function submitRoleAssignment() {
            const userId = document.getElementById('roleModalUserId').value.trim();
            const role = document.getElementById('roleModalRoleSelect').value;
            const permissions = document.getElementById('roleModalPermissions').value.trim();

            const res = await apiCall('/api/admin/role', 'POST', { userId, role, permissions });
            if (res.success) {
                showToast('Role updated successfully!', 'success');
                closeModal('roleModal');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Failed to update role', 'error');
            }
        }

        async function removeRole(userId) {
            if (!confirm('Are you sure you want to revoke administrative role from user ' + userId + '?')) return;
            const res = await apiCall('/api/admin/role/revoke', 'POST', { userId });
            if (res.success) {
                showToast('Role revoked', 'success');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Failed to revoke role', 'error');
            }
        }

        async function toggleBan(userId, isBanned) {
            const res = await apiCall('/api/admin/user/ban', 'POST', { userId, isBanned });
            if (res.success) {
                showToast(isBanned ? 'User banned' : 'User unbanned', 'success');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Action failed', 'error');
            }
        }

        async function closeRoom(roomId) {
            if (!confirm('Are you sure you want to terminate Room #' + roomId + '?')) return;
            const res = await apiCall('/api/admin/room/close', 'POST', { roomId });
            if (res.success) {
                showToast('Room terminated', 'success');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Action failed', 'error');
            }
        }

        async function submitBalanceAdjustment() {
            const userId = document.getElementById('balModalUserId').value.trim();
            const type = document.getElementById('balModalType').value;
            const amount = parseInt(document.getElementById('balModalAmount').value, 10);
            const reason = document.getElementById('balModalReason').value.trim();

            if (!reason) { showToast('Audit reason is required!', 'error'); return; }

            const res = await apiCall('/api/admin/balance', 'POST', { userId, type, amount, reason });
            if (res.success) {
                showToast('Balance updated and logged!', 'success');
                closeModal('balanceModal');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Failed to adjust balance', 'error');
            }
        }

        async function resolveReport(reportId) {
            const res = await apiCall('/api/admin/report/resolve', 'POST', { reportId, status: 'Resolved' });
            if (res.success) {
                showToast('Report resolved', 'success');
                refreshCurrentTab();
            } else {
                showToast(res.message || 'Failed to resolve report', 'error');
            }
        }

        async function saveGeneralConfigs() {
            const updates = [
                { key: 'app_name', value: document.getElementById('cfg_app_name').value, category: 'branding' },
                { key: 'welcome_message', value: document.getElementById('cfg_welcome_message').value, category: 'general' },
                { key: 'announcement_banner', value: document.getElementById('cfg_announcement_banner').value, category: 'general' },
                { key: 'support_email', value: document.getElementById('cfg_support_email').value, category: 'general' }
            ];
            for (const u of updates) {
                await apiCall('/api/admin/config', 'POST', u);
            }
            showToast('General configs saved to database!', 'success');
        }

        async function saveFeatureSwitches() {
            const updates = [
                { key: 'feature_voice_rooms', value: document.getElementById('cfg_feature_voice_rooms').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_live_chat', value: document.getElementById('cfg_feature_live_chat').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_moments', value: document.getElementById('cfg_feature_moments').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_coin_reseller', value: document.getElementById('cfg_feature_coin_reseller').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_maintenance_mode', value: document.getElementById('cfg_feature_maintenance_mode').checked ? 'true' : 'false', category: 'features' }
            ];
            for (const u of updates) {
                await apiCall('/api/admin/config', 'POST', u);
            }
            showToast('Feature switches updated in database!', 'success');
        }
    </script>
</body>
</html>
"""
}
