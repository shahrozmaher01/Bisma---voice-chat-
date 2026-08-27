package com.example.data.admin

object AdminWebPageTemplate {

    fun getHtml(): String {
        return HTML_CONTENT.replace("@@", "$")
    }

    private const val HTML_CONTENT = """
<!DOCTYPE html>
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

        /* Glassmorphism */
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

        /* Scrollbar */
        ::-webkit-scrollbar {
            width: 6px;
            height: 6px;
        }
        ::-webkit-scrollbar-track {
            background: var(--bg-base);
        }
        ::-webkit-scrollbar-thumb {
            background: #2A364F;
            border-radius: 4px;
        }

        /* Login Screen */
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

        .form-group {
            text-align: left;
            margin-bottom: 18px;
        }

        .form-label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: var(--text-secondary);
            margin-bottom: 6px;
        }

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

        .btn-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 20px rgba(255, 42, 133, 0.5);
        }

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

        .btn-secondary:hover {
            background: var(--bg-card-hover);
            border-color: var(--primary);
        }

        .btn-danger {
            background: rgba(255, 23, 68, 0.15);
            color: var(--danger);
            border: 1px solid rgba(255, 23, 68, 0.3);
            padding: 8px 14px;
            border-radius: var(--radius-sm);
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
        }
        .btn-danger:hover {
            background: rgba(255, 23, 68, 0.3);
        }

        .btn-success {
            background: rgba(0, 230, 118, 0.15);
            color: var(--emerald);
            border: 1px solid rgba(0, 230, 118, 0.3);
            padding: 8px 14px;
            border-radius: var(--radius-sm);
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
        }
        .btn-success:hover {
            background: rgba(0, 230, 118, 0.3);
        }

        /* App Layout */
        #adminApp {
            display: none;
            min-height: 100vh;
            display: flex;
        }

        /* Sidebar */
        .sidebar {
            width: 260px;
            background: #0E131F;
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            z-index: 100;
            transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .sidebar-brand {
            padding: 24px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            border-bottom: 1px solid var(--border-color);
        }

        .sidebar-brand-logo {
            width: 38px;
            height: 38px;
            border-radius: 10px;
            background: linear-gradient(135deg, #FF2A85, #FFD700);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .sidebar-menu {
            flex: 1;
            padding: 16px 12px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .menu-category {
            font-size: 11px;
            font-weight: 800;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.8px;
            padding: 12px 12px 6px;
        }

        .menu-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 11px 14px;
            border-radius: var(--radius-sm);
            color: var(--text-secondary);
            font-size: 13.5px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.15s ease;
            text-decoration: none;
        }

        .menu-item:hover {
            background: var(--bg-card);
            color: var(--text-primary);
        }

        .menu-item.active {
            background: rgba(255, 42, 133, 0.15);
            color: #FFFFFF;
            border: 1px solid var(--border-accent);
            font-weight: 700;
        }

        .menu-item-icon {
            font-size: 18px;
            width: 24px;
            text-align: center;
        }

        .sidebar-footer {
            padding: 16px;
            border-top: 1px solid var(--border-color);
            background: rgba(0,0,0,0.2);
        }

        .admin-profile-card {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Main Wrapper */
        .main-wrapper {
            flex: 1;
            margin-left: 260px;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            background: var(--bg-base);
        }

        /* Topbar */
        .topbar {
            height: 68px;
            background: rgba(14, 19, 31, 0.8);
            backdrop-filter: blur(10px);
            border-bottom: 1px solid var(--border-color);
            padding: 0 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 90;
        }

        .mobile-menu-btn {
            display: none;
            background: none;
            border: none;
            color: white;
            font-size: 24px;
            cursor: pointer;
        }

        .topbar-title-wrap h2 {
            font-size: 18px;
            font-weight: 800;
            color: #FFFFFF;
        }

        .topbar-title-wrap p {
            font-size: 12px;
            color: var(--text-muted);
        }

        .topbar-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .badge-role {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .role-super-admin { background: rgba(255, 215, 0, 0.15); color: var(--gold); border: 1px solid rgba(255, 215, 0, 0.4); }
        .role-admin { background: rgba(255, 42, 133, 0.15); color: var(--primary); border: 1px solid rgba(255, 42, 133, 0.4); }
        .role-manager { background: rgba(0, 229, 255, 0.15); color: var(--cyan); border: 1px solid rgba(0, 229, 255, 0.4); }
        .role-bd { background: rgba(0, 230, 118, 0.15); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.4); }
        .role-agency { background: rgba(168, 85, 247, 0.15); color: #C084FC; border: 1px solid rgba(168, 85, 247, 0.4); }
        .role-reseller { background: rgba(255, 145, 0, 0.15); color: var(--warning); border: 1px solid rgba(255, 145, 0, 0.4); }
        .role-user { background: rgba(148, 163, 184, 0.15); color: var(--text-secondary); border: 1px solid var(--border-color); }

        /* Content Area */
        .content-body {
            padding: 24px;
            flex: 1;
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }

        .stat-card {
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .stat-val {
            font-size: 26px;
            font-weight: 800;
            color: #FFFFFF;
            margin-top: 4px;
        }

        .stat-label {
            font-size: 12px;
            color: var(--text-secondary);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
        }

        /* Table & Data Cards */
        .data-table-container {
            width: 100%;
            overflow-x: auto;
            border-radius: var(--radius-md);
            border: 1px solid var(--border-color);
            background: var(--bg-surface);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 13.5px;
        }

        th {
            background: #161F31;
            padding: 14px 16px;
            color: var(--text-secondary);
            font-weight: 700;
            border-bottom: 1px solid var(--border-color);
            white-space: nowrap;
        }

        td {
            padding: 14px 16px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.04);
            color: var(--text-primary);
            vertical-align: middle;
        }

        tr:hover td {
            background: rgba(255, 255, 255, 0.02);
        }

        /* Modals */
        .modal-backdrop {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.75);
            backdrop-filter: blur(8px);
            z-index: 1000;
            align-items: center;
            justify-content: center;
            padding: 16px;
        }

        .modal-box {
            background: var(--bg-surface);
            border: 1px solid var(--border-accent);
            border-radius: var(--radius-lg);
            width: 100%;
            max-width: 520px;
            padding: 26px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.8);
            animation: modalFadeIn 0.2s ease-out;
        }

        @keyframes modalFadeIn {
            from { opacity: 0; transform: scale(0.95); }
            to { opacity: 1; transform: scale(1); }
        }

        .modal-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            border-bottom: 1px solid var(--border-color);
            padding-bottom: 12px;
        }

        .modal-title {
            font-size: 17px;
            font-weight: 800;
        }

        .close-btn {
            background: none;
            border: none;
            color: var(--text-muted);
            font-size: 22px;
            cursor: pointer;
        }
        .close-btn:hover { color: white; }

        /* Toast Notifications */
        #toastContainer {
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 2000;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .toast {
            padding: 14px 18px;
            border-radius: var(--radius-sm);
            background: var(--bg-card);
            border: 1px solid var(--border-accent);
            color: #FFFFFF;
            font-size: 13px;
            font-weight: 600;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideIn 0.2s ease-out;
        }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        /* Toggle Switches */
        .switch {
            position: relative;
            display: inline-block;
            width: 44px;
            height: 24px;
        }

        .switch input { opacity: 0; width: 0; height: 0; }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #2A364F;
            transition: .3s;
            border-radius: 24px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: var(--primary);
        }

        input:checked + .slider:before {
            transform: translateX(20px);
        }

        /* Responsive Viewports */
        @media (max-width: 900px) {
            .sidebar {
                transform: translateX(-100%);
            }
            .sidebar.open {
                transform: translateX(0);
            }
            .main-wrapper {
                margin-left: 0;
            }
            .mobile-menu-btn {
                display: block;
            }
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 540px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }
            .content-body {
                padding: 14px;
            }
        }
    </style>
</head>
<body>

    <div id="toastContainer"></div>

    <div id="loginScreen">
        <div class="login-box">
            <div class="brand-badge">🛡️ Super Admin Control Center</div>
            <h1 style="font-size: 24px; font-weight: 800; margin-bottom: 8px;">Bisma Live Platform</h1>
            <p style="color: var(--text-secondary); font-size: 13px; margin-bottom: 26px;">
                Secure role-based dashboard for authorized platform administrators & operations staff.
            </p>

            <form id="adminLoginForm" onsubmit="handleLogin(event)">
                <div class="form-group">
                    <label class="form-label">Administrator User ID or Email</label>
                    <input type="text" id="loginId" class="form-input" placeholder="e.g. 100001 or admin@bismalive.com" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Account Password</label>
                    <input type="password" id="loginPassword" class="form-input" placeholder="Enter administrative password" required>
                </div>
                <button type="submit" class="btn-primary" id="loginSubmitBtn">
                    <span>Secure Authenticate & Enter</span>
                    <span>→</span>
                </button>
            </form>

            <div style="margin-top: 22px; font-size: 11.5px; color: var(--text-muted); line-height: 1.5;">
                🔒 Protected by End-to-End Cryptographic Token Sessions & Server-Side RBAC Enforcement.
            </div>
        </div>
    </div>

    <div id="adminApp">
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-logo">👑</div>
                <div>
                    <div style="font-weight: 800; font-size: 15px; letter-spacing: -0.2px;">Bisma Live Admin</div>
                    <div style="font-size: 11px; color: var(--gold); font-weight: 700;">Super Control Center</div>
                </div>
            </div>

            <div class="sidebar-menu">
                <div class="menu-category">Overview</div>
                <a class="menu-item active" onclick="switchTab('dashboard')">
                    <span class="menu-item-icon">📊</span>
                    <span>Dashboard</span>
                </a>

                <div class="menu-category">Staff & Access Control</div>
                <a class="menu-item" onclick="switchTab('users')">
                    <span class="menu-item-icon">👥</span>
                    <span>User Management</span>
                </a>
                <a class="menu-item" onclick="switchTab('roles')">
                    <span class="menu-item-icon">🛡️</span>
                    <span>Roles & Hierarchy</span>
                </a>
                <a class="menu-item" onclick="switchTab('superadmins')">
                    <span class="menu-item-icon">👑</span>
                    <span>Super Admins</span>
                </a>
                <a class="menu-item" onclick="switchTab('staff')">
                    <span class="menu-item-icon">👔</span>
                    <span>Staff & Resellers</span>
                </a>

                <div class="menu-category">Operations & Moderation</div>
                <a class="menu-item" onclick="switchTab('rooms')">
                    <span class="menu-item-icon">🎙️</span>
                    <span>Voice Rooms</span>
                </a>
                <a class="menu-item" onclick="switchTab('coins')">
                    <span class="menu-item-icon">🪙</span>
                    <span>Coins & Diamonds</span>
                </a>
                <a class="menu-item" onclick="switchTab('reports')">
                    <span class="menu-item-icon">🚨</span>
                    <span>Reports & Flags</span>
                </a>

                <div class="menu-category">Platform Configuration</div>
                <a class="menu-item" onclick="switchTab('appcontrol')">
                    <span class="menu-item-icon">⚙️</span>
                    <span>App Control Center</span>
                </a>
                <a class="menu-item" onclick="switchTab('branding')">
                    <span class="menu-item-icon">🎨</span>
                    <span>Branding & Layout</span>
                </a>
                <a class="menu-item" onclick="switchTab('audit')">
                    <span class="menu-item-icon">📜</span>
                    <span>Audit Logs</span>
                </a>
            </div>

            <div class="sidebar-footer">
                <div class="admin-profile-card">
                    <div id="sidebarUserAvatarWrap" style="width: 38px; height: 38px; border-radius: 50%; background: #FF2A85; display:flex; align-items:center; justify-content:center; font-weight:800;">A</div>
                    <div style="flex: 1; min-width: 0;">
                        <div id="sidebarAdminName" style="font-weight: 700; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Admin</div>
                        <div id="sidebarAdminRole" class="badge-role role-super-admin" style="font-size: 10px; display: inline-block; padding: 2px 6px;">SUPER ADMIN</div>
                    </div>
                    <button onclick="handleLogout()" style="background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:18px;" title="Logout">🚪</button>
                </div>
            </div>
        </aside>

        <div class="main-wrapper">
            <header class="topbar">
                <div style="display: flex; align-items: center; gap: 14px;">
                    <button class="mobile-menu-btn" onclick="toggleSidebar()">☰</button>
                    <div class="topbar-title-wrap">
                        <h2 id="pageHeading">Platform Dashboard</h2>
                        <p id="pageSubHeading">Real-time telemetry and platform metrics</p>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button class="btn-secondary" onclick="refreshCurrentTab()" style="font-size: 12px;">
                        <span>🔄 Refresh</span>
                    </button>
                    <div id="topbarRoleBadge" class="badge-role role-super-admin">SUPER ADMIN</div>
                </div>
            </header>

            <main class="content-body" id="tabContent">
            </main>
        </div>
    </div>

    <div class="modal-backdrop" id="actionModal">
        <div class="modal-box" id="modalBoxContent">
        </div>
    </div>

    <script>
        let currentSession = null;
        let activeTab = 'dashboard';
        let cachedStats = null;
        let cachedUsers = [];
        let cachedRooms = [];
        let cachedConfigs = [];
        let cachedReports = [];

        window.addEventListener('DOMContentLoaded', () => {
            const savedToken = localStorage.getItem('bisma_admin_token');
            if (savedToken) {
                verifySession(savedToken);
            }
        });

        function showToast(msg, type = 'info') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = 'toast';
            const icon = type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️';
            toast.innerHTML = '<span>' + icon + '</span><span>' + msg + '</span>';
            container.appendChild(toast);
            setTimeout(() => {
                toast.style.opacity = '0';
                setTimeout(() => toast.remove(), 300);
            }, 3500);
        }

        async function handleLogin(e) {
            e.preventDefault();
            const idOrEmail = document.getElementById('loginId').value.trim();
            const password = document.getElementById('loginPassword').value;
            const btn = document.getElementById('loginSubmitBtn');
            btn.disabled = true;
            btn.innerText = 'Verifying Authenticity...';

            try {
                const res = await fetch('/api/admin/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ idOrEmail, password })
                });
                const data = await res.json();
                if (data.success) {
                    currentSession = data.session;
                    localStorage.setItem('bisma_admin_token', data.session.token);
                    showToast('Authenticated successfully as ' + data.session.role, 'success');
                    showAdminApp();
                } else {
                    showToast(data.message || 'Authentication failed', 'error');
                }
            } catch (err) {
                showToast('Network / Server Error: ' + err.message, 'error');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>Secure Authenticate & Enter</span><span>→</span>';
            }
        }

        async function verifySession(token) {
            try {
                const res = await fetch('/api/admin/session', {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                const data = await res.json();
                if (data.success && data.session) {
                    currentSession = data.session;
                    showAdminApp();
                } else {
                    localStorage.removeItem('bisma_admin_token');
                }
            } catch (e) {
                console.error(e);
            }
        }

        function showAdminApp() {
            document.getElementById('loginScreen').style.display = 'none';
            document.getElementById('adminApp').style.display = 'flex';
            document.getElementById('sidebarAdminName').innerText = currentSession.username;
            document.getElementById('sidebarAdminRole').innerText = currentSession.role.toUpperCase();
            document.getElementById('sidebarAdminRole').className = 'badge-role role-' + currentSession.role.toLowerCase().replace(' ', '-');
            document.getElementById('topbarRoleBadge').innerText = currentSession.role.toUpperCase();
            document.getElementById('topbarRoleBadge').className = 'badge-role role-' + currentSession.role.toLowerCase().replace(' ', '-');
            switchTab('dashboard');
        }

        async function handleLogout() {
            if (currentSession && currentSession.token) {
                await fetch('/api/admin/logout', {
                    method: 'POST',
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
            }
            localStorage.removeItem('bisma_admin_token');
            currentSession = null;
            document.getElementById('adminApp').style.display = 'none';
            document.getElementById('loginScreen').style.display = 'flex';
            showToast('Logged out securely', 'info');
        }

        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('open');
        }

        function switchTab(tab) {
            activeTab = tab;
            document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
            document.getElementById('sidebar').classList.remove('open');

            const content = document.getElementById('tabContent');
            const heading = document.getElementById('pageHeading');
            const subheading = document.getElementById('pageSubHeading');

            switch (tab) {
                case 'dashboard':
                    heading.innerText = 'Platform Overview';
                    subheading.innerText = 'Live database telemetry, active voice rooms and staff summaries';
                    renderDashboardTab();
                    break;
                case 'users':
                    heading.innerText = 'User Management';
                    subheading.innerText = 'Inspect real profiles, change roles, moderate accounts and balances';
                    renderUsersTab();
                    break;
                case 'roles':
                    heading.innerText = 'Roles & RBAC Matrix';
                    subheading.innerText = 'Hierarchical role levels, permission assignments, and privilege guards';
                    renderRolesTab();
                    break;
                case 'superadmins':
                    heading.innerText = 'Super Admin Management';
                    subheading.innerText = 'High-security root management with confirmation safeguards';
                    renderSuperAdminsTab();
                    break;
                case 'staff':
                    heading.innerText = 'Staff & Reseller Management';
                    subheading.innerText = 'Admins, Managers, BDs, Agencies, and Coin Reseller assignments';
                    renderStaffTab();
                    break;
                case 'rooms':
                    heading.innerText = 'Voice Room Moderation';
                    subheading.innerText = 'Real-time active room status, seat monitoring, and lock controls';
                    renderRoomsTab();
                    break;
                case 'coins':
                    heading.innerText = 'Coins & Diamond Ledger';
                    subheading.innerText = 'Balance adjustments, reseller quotas, and wallet audit trail';
                    renderCoinsTab();
                    break;
                case 'reports':
                    heading.innerText = 'Reports & Content Flags';
                    subheading.innerText = 'Pending user flags, room complaints, and resolution history';
                    renderReportsTab();
                    break;
                case 'appcontrol':
                    heading.innerText = 'App Control Center';
                    subheading.innerText = 'Global switches, maintenance mode, voice settings, and contacts';
                    renderAppControlTab();
                    break;
                case 'branding':
                    heading.innerText = 'Branding & Frame Layout';
                    subheading.innerText = 'Themes, accent colors, logos, splash banners, and predefined layouts';
                    renderBrandingTab();
                    break;
                case 'audit':
                    heading.innerText = 'Audit Trail & Compliance';
                    subheading.innerText = 'Immutable administrative action logs with previous and new values';
                    renderAuditTab();
                    break;
            }
        }

        function refreshCurrentTab() {
            switchTab(activeTab);
            showToast('Refreshed live data', 'info');
        }

        async function renderDashboardTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Fetching live telemetry...</div>';

            try {
                const res = await fetch('/api/admin/dashboard', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const data = await res.json();
                cachedStats = data;

                container.innerHTML = `
                    <div class="stats-grid">
                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Total Real Users</div>
                                <div class="stat-val">@@{data.totalUsers}</div>
                                <div style="font-size:11px; color:var(--emerald); margin-top:4px;">● @@{data.activeUsers} Active Accounts</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(255, 42, 133, 0.15); color:var(--primary);">👥</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Active Voice Rooms</div>
                                <div class="stat-val">@@{data.activeRooms}</div>
                                <div style="font-size:11px; color:var(--cyan); margin-top:4px;">● Live Audio Channels</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(0, 229, 255, 0.15); color:var(--cyan);">🎙️</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Super Admins</div>
                                <div class="stat-val">@@{data.superAdminsCount}</div>
                                <div style="font-size:11px; color:var(--gold); margin-top:4px;">● Root Tier 100</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(255, 215, 0, 0.15); color:var(--gold);">👑</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Platform Admins</div>
                                <div class="stat-val">@@{data.adminsCount}</div>
                                <div style="font-size:11px; color:var(--text-secondary); margin-top:4px;">● Ops Tier 80</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(255, 42, 133, 0.15); color:var(--primary);">🛡️</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Room Managers</div>
                                <div class="stat-val">@@{data.managersCount}</div>
                                <div style="font-size:11px; color:var(--cyan); margin-top:4px;">● Moderation Tier 60</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(0, 229, 255, 0.15); color:var(--cyan);">👔</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Agencies & BD</div>
                                <div class="stat-val">@@{data.agenciesCount + data.bdsCount}</div>
                                <div style="font-size:11px; color:var(--emerald); margin-top:4px;">@@{data.agenciesCount} Agencies | @@{data.bdsCount} BD</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(0, 230, 118, 0.15); color:var(--emerald);">🏢</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Coin Resellers</div>
                                <div class="stat-val">@@{data.resellersCount}</div>
                                <div style="font-size:11px; color:var(--warning); margin-top:4px;">● Authorized Distribution</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(255, 145, 0, 0.15); color:var(--warning);">🪙</div>
                        </div>

                        <div class="glass-card stat-card">
                            <div>
                                <div class="stat-label">Pending Reports</div>
                                <div class="stat-val" style="color:@@{data.pendingReports > 0 ? 'var(--danger)' : 'var(--emerald)'};">@@{data.pendingReports}</div>
                                <div style="font-size:11px; color:var(--text-muted); margin-top:4px;">Requires Review</div>
                            </div>
                            <div class="stat-icon" style="background:rgba(255, 23, 68, 0.15); color:var(--danger);">🚨</div>
                        </div>
                    </div>

                    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap:20px;">
                        <div class="glass-panel" style="padding:20px;">
                            <h3 style="font-size:15px; font-weight:800; margin-bottom:16px;">⚡ Quick Operational Shortcuts</h3>
                            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:10px;">
                                <button class="btn-secondary" onclick="switchTab('users')" style="justify-content:flex-start;">👥 Search Users</button>
                                <button class="btn-secondary" onclick="switchTab('superadmins')" style="justify-content:flex-start;">👑 Super Admins</button>
                                <button class="btn-secondary" onclick="switchTab('rooms')" style="justify-content:flex-start;">🎙️ Moderate Rooms</button>
                                <button class="btn-secondary" onclick="switchTab('appcontrol')" style="justify-content:flex-start;">⚙️ App Config</button>
                                <button class="btn-secondary" onclick="switchTab('reports')" style="justify-content:flex-start;">🚨 Review Reports (@@{data.pendingReports})</button>
                                <button class="btn-secondary" onclick="switchTab('coins')" style="justify-content:flex-start;">🪙 Coins Adjust</button>
                            </div>
                        </div>

                        <div class="glass-panel" style="padding:20px;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
                                <h3 style="font-size:15px; font-weight:800;">📜 Live Administrative Stream</h3>
                                <button class="btn-secondary" style="padding:4px 10px; font-size:11px;" onclick="switchTab('audit')">View All</button>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:10px; max-height:260px; overflow-y:auto;">
                                @@{(data.recentLogs || []).map(log => `
                                    <div style="padding:10px; background:var(--bg-card); border-radius:var(--radius-sm); font-size:12px; display:flex; justify-content:space-between; align-items:center;">
                                        <div>
                                            <span style="font-weight:700; color:var(--primary);">@@{log.action}</span>
                                            <span style="color:var(--text-secondary);"> by @@{log.adminName}</span>
                                            <div style="color:var(--text-muted); font-size:11px; margin-top:2px;">Target: @@{log.targetName} (@@{log.targetId})</div>
                                        </div>
                                        <div style="font-size:10px; color:var(--text-muted); text-align:right;">
                                            @@{new Date(log.timestamp).toLocaleTimeString()}
                                        </div>
                                    </div>
                                `).join('') || '<div style="color:var(--text-muted); font-size:12px;">No recent administrative actions recorded.</div>'}
                            </div>
                        </div>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">Error loading stats: ' + e.message + '</div>';
            }
        }

        async function renderUsersTab(query = '', roleFilter = '') {
            const container = document.getElementById('tabContent');
            container.innerHTML = `
                <div class="glass-panel" style="padding:18px; margin-bottom:20px;">
                    <div style="display:flex; flex-wrap:wrap; gap:12px; align-items:center; justify-content:space-between;">
                        <div style="display:flex; gap:10px; flex:1; min-width:280px;">
                            <input type="text" id="userSearchInput" class="form-input" placeholder="Search by User ID, Name, or Email..." value="@@{query}" onkeydown="if(event.key==='Enter') searchUsersAction()">
                            <button class="btn-primary" style="width:auto;" onclick="searchUsersAction()">Search</button>
                        </div>
                        <div style="display:flex; gap:8px;">
                            <select id="userRoleFilter" class="form-select" style="width:auto;" onchange="searchUsersAction()">
                                <option value="">All Roles</option>
                                <option value="Super Admin" @@{roleFilter==='Super Admin'?'selected':''}>Super Admin</option>
                                <option value="Admin" @@{roleFilter==='Admin'?'selected':''}>Admin</option>
                                <option value="Manager" @@{roleFilter==='Manager'?'selected':''}>Manager</option>
                                <option value="BD" @@{roleFilter==='BD'?'selected':''}>BD</option>
                                <option value="Agency" @@{roleFilter==='Agency'?'selected':''}>Agency</option>
                                <option value="Coin Reseller" @@{roleFilter==='Coin Reseller'?'selected':''}>Coin Reseller</option>
                                <option value="User" @@{roleFilter==='User'?'selected':''}>User</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="data-table-container">
                    <table id="usersTable">
                        <thead>
                            <tr>
                                <th>User Profile</th>
                                <th>User ID</th>
                                <th>Assigned Role</th>
                                <th>Wallet (Coins / Dia)</th>
                                <th>VIP / Level</th>
                                <th>Status</th>
                                <th>Administrative Actions</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">Loading real users...</td></tr>
                        </tbody>
                    </table>
                </div>
            `;

            try {
                const res = await fetch('/api/admin/users?query=' + encodeURIComponent(query) + '&role=' + encodeURIComponent(roleFilter), {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const users = await res.json();
                cachedUsers = users;
                const tbody = document.getElementById('usersTableBody');

                if (users.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No users found matching query.</td></tr>';
                    return;
                }

                tbody.innerHTML = users.map(u => `
                    <tr>
                        <td>
                            <div style="display:flex; align-items:center; gap:10px;">
                                <img src="@@{u.avatarUrl}" style="width:36px; height:36px; border-radius:50%; object-fit:cover; border:1px solid var(--border-color);" onerror="this.src='https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'">
                                <div>
                                    <div style="font-weight:700;">@@{u.username}</div>
                                    <div style="font-size:11px; color:var(--text-muted);">@@{u.email || u.country}</div>
                                </div>
                            </div>
                        </td>
                        <td><span style="font-family:'JetBrains Mono'; font-weight:700; color:var(--cyan);">@@{u.id}</span></td>
                        <td>
                            <span class="badge-role role-@@{u.role.toLowerCase().replace(' ', '-')}">@@{u.role}</span>
                        </td>
                        <td>
                            <div style="font-weight:700; color:var(--gold);">🪙 @@{u.coins.toLocaleString()}</div>
                            <div style="font-size:11px; color:var(--cyan);">💎 @@{u.diamonds.toLocaleString()}</div>
                        </td>
                        <td>
                            <span style="color:var(--gold); font-weight:700;">VIP @@{u.vipLevel}</span>
                            <span style="color:var(--text-muted); font-size:11px;">(Lv @@{u.userLevel})</span>
                        </td>
                        <td>
                            <span style="color:@@{u.isBanned ? 'var(--danger)' : 'var(--emerald)'}; font-weight:700; font-size:12px;">
                                @@{u.isBanned ? '🚫 SUSPENDED' : '🟢 ACTIVE'}
                            </span>
                        </td>
                        <td>
                            <div style="display:flex; gap:6px; flex-wrap:wrap;">
                                <button class="btn-secondary" style="padding:5px 9px; font-size:11px;" onclick="openRoleModal('@@{u.id}', '@@{u.username}', '@@{u.role}')">Role</button>
                                <button class="btn-secondary" style="padding:5px 9px; font-size:11px;" onclick="openBalanceModal('@@{u.id}', '@@{u.username}')">Coins</button>
                                @@{u.isBanned ? `
                                    <button class="btn-success" style="padding:5px 9px; font-size:11px;" onclick="toggleUserBan('@@{u.id}', false)">Unban</button>
                                ` : `
                                    <button class="btn-danger" style="padding:5px 9px; font-size:11px;" onclick="toggleUserBan('@@{u.id}', true)">Ban</button>
                                `}
                            </div>
                        </td>
                    </tr>
                `).join('');
            } catch (e) {
                console.error(e);
            }
        }

        function searchUsersAction() {
            const q = document.getElementById('userSearchInput').value.trim();
            const r = document.getElementById('userRoleFilter').value;
            renderUsersTab(q, r);
        }

        function renderRolesTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = `
                <div class="glass-panel" style="padding:22px; margin-bottom:24px;">
                    <h3 style="font-size:16px; font-weight:800; margin-bottom:8px;">🛡️ Role Hierarchy & Permission Levels</h3>
                    <p style="color:var(--text-secondary); font-size:13px; margin-bottom:20px;">
                        Strict server-side validation enforces that lower-tier roles cannot assign, elevate, or modify accounts at or above their rank.
                    </p>

                    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap:16px;">
                        <div class="glass-card" style="padding:16px; border-left:4px solid var(--gold);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-super-admin">Super Admin</span>
                                <span style="font-weight:800; color:var(--gold); font-size:12px;">LEVEL 100</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Root administrator. Holds absolute control over roles, configuration, branding, database balances, and staff assignments.
                            </p>
                        </div>

                        <div class="glass-card" style="padding:16px; border-left:4px solid var(--primary);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-admin">Admin</span>
                                <span style="font-weight:800; color:var(--primary); font-size:12px;">LEVEL 80</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Senior operations. Can manage Managers, BDs, Agencies, Coin Resellers, user bans, and voice rooms. Cannot touch Super Admins.
                            </p>
                        </div>

                        <div class="glass-card" style="padding:16px; border-left:4px solid var(--cyan);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-manager">Manager</span>
                                <span style="font-weight:800; color:var(--cyan); font-size:12px;">LEVEL 60</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Room moderation and user conflict resolution. Cannot alter staff roles or wallet balances.
                            </p>
                        </div>

                        <div class="glass-card" style="padding:16px; border-left:4px solid var(--emerald);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-bd">BD (Business Dev)</span>
                                <span style="font-weight:800; color:var(--emerald); font-size:12px;">LEVEL 50</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Onboarding agencies, managing partner performance, and reseller liaison.
                            </p>
                        </div>

                        <div class="glass-card" style="padding:16px; border-left:4px solid #C084FC;">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-agency">Agency</span>
                                <span style="font-weight:800; color:#C084FC; font-size:12px;">LEVEL 40</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Host recruitment, agency family events, and broadcaster commission tracking.
                            </p>
                        </div>

                        <div class="glass-card" style="padding:16px; border-left:4px solid var(--warning);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="badge-role role-reseller">Coin Reseller</span>
                                <span style="font-weight:800; color:var(--warning); font-size:12px;">LEVEL 30</span>
                            </div>
                            <p style="font-size:12px; color:var(--text-secondary); margin-top:8px;">
                                Authorized coin merchant. Can transfer coins within authorized quotas with full audit tracking.
                            </p>
                        </div>
                    </div>
                </div>
            `;
        }

        async function renderSuperAdminsTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = `
                <div class="glass-panel" style="padding:22px; margin-bottom:24px; border:1px solid rgba(255, 215, 0, 0.4);">
                    <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px;">
                        <div>
                            <h3 style="font-size:17px; font-weight:800; color:var(--gold);">👑 Super Admin Management</h3>
                            <p style="color:var(--text-secondary); font-size:13px; margin-top:4px;">
                                Only authenticated Super Admins can add or remove Super Admin privileges. Explicit confirmation is required.
                            </p>
                        </div>
                        <button class="btn-primary" style="width:auto; background:linear-gradient(135deg, #FFD700, #FFA000); color:#000;" onclick="openPromoteSuperAdminModal()">
                            + Add Super Admin
                        </button>
                    </div>
                </div>

                <div class="data-table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Super Administrator</th>
                                <th>User ID</th>
                                <th>Assigned By</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody id="superAdminTableBody">
                            <tr><td colspan="5" style="text-align:center; padding:30px; color:var(--text-muted);">Loading Super Admins...</td></tr>
                        </tbody>
                    </table>
                </div>
            `;

            try {
                const res = await fetch('/api/admin/users?role=Super%20Admin', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const admins = await res.json();
                const tbody = document.getElementById('superAdminTableBody');

                if (admins.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding:30px; color:var(--text-muted);">No Super Admins found.</td></tr>';
                    return;
                }

                tbody.innerHTML = admins.map(a => `
                    <tr>
                        <td>
                            <div style="display:flex; align-items:center; gap:10px;">
                                <img src="@@{a.avatarUrl}" style="width:36px; height:36px; border-radius:50%; border:2px solid var(--gold);" onerror="this.src='https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'">
                                <div>
                                    <div style="font-weight:800; color:var(--gold);">@@{a.username}</div>
                                    <div style="font-size:11px; color:var(--text-muted);">@@{a.email || 'Root User'}</div>
                                </div>
                            </div>
                        </td>
                        <td><span style="font-family:'JetBrains Mono'; font-weight:700; color:var(--gold);">@@{a.id}</span></td>
                        <td><span style="color:var(--text-secondary); font-size:12px;">Root Initializer</span></td>
                        <td><span class="badge-role role-super-admin">ROOT PRIVILEGES</span></td>
                        <td>
                            @@{currentSession.userId === a.id ? `
                                <span style="font-size:11px; color:var(--text-muted);">(Current Session)</span>
                            ` : `
                                <button class="btn-danger" onclick="revokeSuperAdminConfirm('@@{a.id}', '@@{a.username}')">Revoke</button>
                            `}
                        </td>
                    </tr>
                `).join('');
            } catch (e) {
                console.error(e);
            }
        }

        async function renderStaffTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = `
                <div class="glass-panel" style="padding:18px; margin-bottom:20px;">
                    <div style="display:flex; gap:10px; flex-wrap:wrap;">
                        <button class="btn-secondary" onclick="renderUsersTab('', 'Admin')">🛡️ Platform Admins</button>
                        <button class="btn-secondary" onclick="renderUsersTab('', 'Manager')">👔 Room Managers</button>
                        <button class="btn-secondary" onclick="renderUsersTab('', 'BD')">🤝 BD Partners</button>
                        <button class="btn-secondary" onclick="renderUsersTab('', 'Agency')">🏢 Agencies</button>
                        <button class="btn-secondary" onclick="renderUsersTab('', 'Coin Reseller')">🪙 Coin Resellers</button>
                    </div>
                </div>
            `;
            renderUsersTab('', 'Admin');
        }

        async function renderRoomsTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Loading active voice rooms...</div>';

            try {
                const res = await fetch('/api/admin/rooms', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const rooms = await res.json();
                cachedRooms = rooms;

                container.innerHTML = `
                    <div class="data-table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Room Name & Cover</th>
                                    <th>Room ID</th>
                                    <th>Host / Owner</th>
                                    <th>Category</th>
                                    <th>Seats</th>
                                    <th>Status</th>
                                    <th>Moderation Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                @@{rooms.length === 0 ? '<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No live rooms found.</td></tr>' : ''}
                                @@{rooms.map(r => `
                                    <tr>
                                        <td>
                                            <div style="display:flex; align-items:center; gap:10px;">
                                                <img src="@@{r.coverUrl}" style="width:40px; height:40px; border-radius:8px; object-fit:cover;" onerror="this.src='https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=100'">
                                                <div>
                                                    <div style="font-weight:700;">@@{r.title}</div>
                                                    <div style="font-size:11px; color:var(--text-muted); max-width:200px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">@@{r.announcement}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td><span style="font-family:'JetBrains Mono'; font-weight:700; color:var(--cyan);">@@{r.id}</span></td>
                                        <td>
                                            <div style="font-weight:600;">@@{r.ownerName}</div>
                                            <div style="font-size:11px; color:var(--text-muted);">ID: @@{r.ownerId}</div>
                                        </td>
                                        <td><span class="badge-role role-manager">@@{r.category}</span></td>
                                        <td><span style="font-weight:700;">@@{r.seatCount} Seats</span></td>
                                        <td>
                                            <span style="color:@@{r.isLocked ? 'var(--warning)' : 'var(--emerald)'}; font-weight:700; font-size:12px;">
                                                @@{r.isLocked ? '🔒 LOCKED' : '🟢 OPEN'}
                                            </span>
                                        </td>
                                        <td>
                                            <div style="display:flex; gap:6px;">
                                                @@{r.isLocked ? `
                                                    <button class="btn-success" style="padding:4px 8px; font-size:11px;" onclick="moderateRoomAction('@@{r.id}', 'UNLOCK')">Unlock</button>
                                                ` : `
                                                    <button class="btn-secondary" style="padding:4px 8px; font-size:11px;" onclick="moderateRoomAction('@@{r.id}', 'LOCK')">Lock</button>
                                                `}
                                                <button class="btn-secondary" style="padding:4px 8px; font-size:11px;" onclick="openAnnouncementModal('@@{r.id}', '@@{escapeHtml(r.announcement)}')">Edit Notice</button>
                                                <button class="btn-danger" style="padding:4px 8px; font-size:11px;" onclick="moderateRoomAction('@@{r.id}', 'CLOSE')">End Room</button>
                                            </div>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">' + e.message + '</div>';
            }
        }

        async function renderCoinsTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = `
                <div class="glass-panel" style="padding:22px; margin-bottom:24px;">
                    <h3 style="font-size:16px; font-weight:800; margin-bottom:8px;">🪙 Coin & Diamond Wallet Operations</h3>
                    <p style="color:var(--text-secondary); font-size:13px; margin-bottom:18px;">
                        All administrative balance grants or deductions require an authorized reason and are permanently recorded into immutable audit logs.
                    </p>

                    <div class="form-group" style="max-width:400px;">
                        <label class="form-label">Search User ID to Adjust Balance</label>
                        <div style="display:flex; gap:8px;">
                            <input type="text" id="quickCoinUserId" class="form-input" placeholder="Enter target User ID">
                            <button class="btn-primary" style="width:auto;" onclick="openBalanceForTypedUser()">Lookup</button>
                        </div>
                    </div>
                </div>
            `;
            renderUsersTab();
        }

        function openBalanceForTypedUser() {
            const uid = document.getElementById('quickCoinUserId').value.trim();
            if (!uid) return showToast('Please enter a User ID', 'error');
            openBalanceModal(uid, 'User ' + uid);
        }

        async function renderReportsTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Loading moderation reports...</div>';

            try {
                const res = await fetch('/api/admin/reports', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const reports = await res.json();
                cachedReports = reports;

                container.innerHTML = `
                    <div class="data-table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Reported Target</th>
                                    <th>Reporter</th>
                                    <th>Reason & Details</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Moderation Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                @@{reports.length === 0 ? '<tr><td colspan="6" style="text-align:center; padding:30px; color:var(--text-muted);">No reports found. Clean queue! 🎉</td></tr>' : ''}
                                @@{reports.map(rep => `
                                    <tr>
                                        <td>
                                            <div style="font-weight:700; color:var(--primary);">@@{rep.targetType}: @@{rep.targetTitleOrName}</div>
                                            <div style="font-size:11px; color:var(--text-muted);">ID: @@{rep.targetId}</div>
                                        </td>
                                        <td>
                                            <div style="font-weight:600;">@@{rep.reporterName}</div>
                                            <div style="font-size:11px; color:var(--text-muted);">ID: @@{rep.reporterId}</div>
                                        </td>
                                        <td>
                                            <div style="font-weight:700;">@@{rep.reason}</div>
                                            <div style="font-size:12px; color:var(--text-secondary);">@@{rep.details || 'No additional details.'}</div>
                                        </td>
                                        <td><span style="font-size:11px; color:var(--text-muted);">@@{new Date(rep.createdAt).toLocaleString()}</span></td>
                                        <td>
                                            <span style="color:@@{rep.status === 'Pending' ? 'var(--danger)' : 'var(--emerald)'}; font-weight:700; font-size:12px;">
                                                @@{rep.status.toUpperCase()}
                                            </span>
                                        </td>
                                        <td>
                                            @@{rep.status === 'Pending' ? `
                                                <div style="display:flex; gap:6px;">
                                                    <button class="btn-success" style="padding:4px 8px; font-size:11px;" onclick="resolveReportAction('@@{rep.id}', 'Resolved')">Resolve</button>
                                                    <button class="btn-secondary" style="padding:4px 8px; font-size:11px;" onclick="resolveReportAction('@@{rep.id}', 'Dismissed')">Dismiss</button>
                                                </div>
                                            ` : `
                                                <span style="font-size:11px; color:var(--text-muted);">Closed by @@{rep.resolvedBy || 'Admin'}</span>
                                            `}
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">' + e.message + '</div>';
            }
        }

        async function renderAppControlTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Loading platform configurations...</div>';

            try {
                const res = await fetch('/api/admin/config', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const configs = await res.json();
                cachedConfigs = configs;

                const getVal = (k, def = '') => {
                    const c = configs.find(x => x.key === k);
                    return c ? c.value : def;
                };

                container.innerHTML = `
                    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap:20px;">
                        <div class="glass-panel" style="padding:22px;">
                            <h3 style="font-size:16px; font-weight:800; margin-bottom:16px;">🌐 General Platform Info</h3>

                            <div class="form-group">
                                <label class="form-label">Application Name</label>
                                <input type="text" id="cfg_app_name" class="form-input" value="@@{getVal('app_name', 'Bisma Live')}">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Welcome Text</label>
                                <textarea id="cfg_welcome_message" class="form-textarea" rows="2">@@{getVal('welcome_message')}</textarea>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Official Community Banner</label>
                                <textarea id="cfg_announcement_banner" class="form-textarea" rows="2">@@{getVal('announcement_banner')}</textarea>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Support Email</label>
                                <input type="email" id="cfg_support_email" class="form-input" value="@@{getVal('support_email')}">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Support WhatsApp / Helpline</label>
                                <input type="text" id="cfg_support_whatsapp" class="form-input" value="@@{getVal('support_whatsapp')}">
                            </div>

                            <button class="btn-primary" onclick="saveGeneralConfigs()">Save General Settings</button>
                        </div>

                        <div class="glass-panel" style="padding:22px;">
                            <h3 style="font-size:16px; font-weight:800; margin-bottom:16px;">⚡ Feature Switches & Modules</h3>

                            <div style="display:flex; flex-direction:column; gap:14px; margin-bottom:20px;">
                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px;">Voice Rooms Module</div>
                                        <div style="font-size:11px; color:var(--text-muted);">Enable live multi-seat voice rooms</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_voice_rooms" @@{getVal('feature_voice_rooms')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>

                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px;">Live Chat & Messaging</div>
                                        <div style="font-size:11px; color:var(--text-muted);">Enable real-time messaging stream</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_live_chat" @@{getVal('feature_live_chat')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>

                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px;">Lucky 77 & Minigames</div>
                                        <div style="font-size:11px; color:var(--text-muted);">Enable in-app entertainment minigames</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_minigames" @@{getVal('feature_minigames')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>

                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px;">Moments Community Feed</div>
                                        <div style="font-size:11px; color:var(--text-muted);">User posts, likes and comments</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_moments" @@{getVal('feature_moments')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>

                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px;">Coin Reseller Module</div>
                                        <div style="font-size:11px; color:var(--text-muted);">Allow authorized merchant transfers</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_coin_reseller" @@{getVal('feature_coin_reseller')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>

                                <div style="display:flex; justify-content:space-between; align-items:center; padding-top:10px; border-top:1px solid var(--border-color);">
                                    <div>
                                        <div style="font-weight:700; font-size:13.5px; color:var(--danger);">🚨 Maintenance Mode</div>
                                        <div style="font-size:11px; color:var(--text-muted);">Temporarily close rooms for maintenance</div>
                                    </div>
                                    <label class="switch">
                                        <input type="checkbox" id="cfg_feature_maintenance_mode" @@{getVal('feature_maintenance_mode')==='true'?'checked':''}>
                                        <span class="slider"></span>
                                    </label>
                                </div>
                            </div>

                            <button class="btn-primary" onclick="saveFeatureSwitches()">Apply Feature Switches</button>
                        </div>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">' + e.message + '</div>';
            }
        }

        async function renderBrandingTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Loading branding configurations...</div>';

            try {
                const res = await fetch('/api/admin/config', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const configs = await res.json();
                const getVal = (k, def = '') => {
                    const c = configs.find(x => x.key === k);
                    return c ? c.value : def;
                };

                container.innerHTML = `
                    <div class="glass-panel" style="padding:22px; max-width:700px;">
                        <h3 style="font-size:16px; font-weight:800; margin-bottom:6px;">🎨 Branding & Layout Customization</h3>
                        <p style="color:var(--text-secondary); font-size:13px; margin-bottom:20px;">
                            Configure visual appearance through safe predefined templates and color schemes.
                        </p>

                        <div class="form-group">
                            <label class="form-label">Theme Mode Palette</label>
                            <select id="cfg_theme_mode" class="form-select">
                                <option value="Dark Neon" @@{getVal('theme_mode')==='Dark Neon'?'selected':''}>Dark Neon (Pink & Gold Accent)</option>
                                <option value="Cyberpunk Blue" @@{getVal('theme_mode')==='Cyberpunk Blue'?'selected':''}>Cyberpunk Blue (Electric Cyan & Purple)</option>
                                <option value="Royal Gold" @@{getVal('theme_mode')==='Royal Gold'?'selected':''}>Royal Gold (Luxury Midnight & Amber)</option>
                                <option value="Midnight Velvet" @@{getVal('theme_mode')==='Midnight Velvet'?'selected':''}>Midnight Velvet (Deep Indigo & Violet)</option>
                            </select>
                        </div>

                        <div style="display:grid; grid-template-columns: 1fr 1fr; gap:16px;">
                            <div class="form-group">
                                <label class="form-label">Primary Accent Color (Hex)</label>
                                <input type="text" id="cfg_primary_accent_color" class="form-input" value="@@{getVal('primary_accent_color', '#FF2A85')}">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Secondary Accent Color (Hex)</label>
                                <input type="text" id="cfg_secondary_accent_color" class="form-input" value="@@{getVal('secondary_accent_color', '#FFD700')}">
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Logo Image URL</label>
                            <input type="text" id="cfg_app_logo" class="form-input" value="@@{getVal('app_logo')}">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Splash Screen Title</label>
                            <input type="text" id="cfg_splash_title" class="form-input" value="@@{getVal('splash_title')}">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Splash Screen Subtitle</label>
                            <input type="text" id="cfg_splash_subtitle" class="form-input" value="@@{getVal('splash_subtitle')}">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Navigation Bar Style</label>
                            <select id="cfg_navigation_style" class="form-select">
                                <option value="Floating Pill Bar" @@{getVal('navigation_style')==='Floating Pill Bar'?'selected':''}>Floating Pill Glass Bar</option>
                                <option value="Docked Bottom Bar" @@{getVal('navigation_style')==='Docked Bottom Bar'?'selected':''}>Docked Bottom Bar</option>
                            </select>
                        </div>

                        <button class="btn-primary" onclick="saveBrandingConfigs()">Save Branding & Layout</button>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">' + e.message + '</div>';
            }
        }

        async function renderAuditTab() {
            const container = document.getElementById('tabContent');
            container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:40px;">Fetching immutable audit trail...</div>';

            try {
                const res = await fetch('/api/admin/audit-logs', {
                    headers: { 'Authorization': 'Bearer ' + currentSession.token }
                });
                const logs = await res.json();

                container.innerHTML = `
                    <div class="data-table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Timestamp</th>
                                    <th>Admin User</th>
                                    <th>Role</th>
                                    <th>Action</th>
                                    <th>Target</th>
                                    <th>Previous Value</th>
                                    <th>New Value / Reason</th>
                                    <th>IP</th>
                                </tr>
                            </thead>
                            <tbody>
                                @@{logs.length === 0 ? '<tr><td colspan="8" style="text-align:center; padding:30px; color:var(--text-muted);">No audit logs found.</td></tr>' : ''}
                                @@{logs.map(l => `
                                    <tr>
                                        <td><span style="font-size:11px; color:var(--text-muted); font-family:'JetBrains Mono';">@@{new Date(l.timestamp).toLocaleString()}</span></td>
                                        <td><span style="font-weight:700;">@@{l.adminName}</span> <span style="font-size:10px; color:var(--text-muted);">(@@{l.adminId})</span></td>
                                        <td><span class="badge-role role-@@{l.adminRole.toLowerCase().replace(' ', '-')}">@@{l.adminRole}</span></td>
                                        <td><span style="font-weight:700; color:var(--primary); font-family:'JetBrains Mono'; font-size:12px;">@@{l.action}</span></td>
                                        <td><span style="font-weight:600;">@@{l.targetName}</span> <span style="font-size:10px; color:var(--cyan);">[@@{l.targetType}]</span></td>
                                        <td><span style="color:var(--text-muted); font-size:11px;">@@{l.previousValue || '-'}</span></td>
                                        <td><span style="color:var(--emerald); font-size:11px;">@@{l.newValue || '-'}</span></td>
                                        <td><span style="font-family:'JetBrains Mono'; font-size:10px; color:var(--text-muted);">@@{l.ipAddress || '127.0.0.1'}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            } catch (e) {
                container.innerHTML = '<div style="color:var(--danger);">' + e.message + '</div>';
            }
        }

        function closeModal() {
            document.getElementById('actionModal').style.display = 'none';
        }

        function openRoleModal(userId, username, currentRole) {
            const modal = document.getElementById('modalBoxContent');
            modal.innerHTML = `
                <div class="modal-header">
                    <div class="modal-title">Assign Role to ` + username + `</div>
                    <button class="close-btn" onclick="closeModal()">✕</button>
                </div>
                <div class="form-group">
                    <label class="form-label">Target User ID</label>
                    <input type="text" class="form-input" value="` + userId + `" disabled>
                </div>
                <div class="form-group">
                    <label class="form-label">Select Authorized Role</label>
                    <select id="modalRoleSelect" class="form-select">
                        <option value="User" ` + (currentRole==='User'?'selected':'') + `>User (Standard participant)</option>
                        <option value="Coin Reseller" ` + (currentRole==='Coin Reseller'?'selected':'') + `>Coin Reseller (Tier 30)</option>
                        <option value="Agency" ` + (currentRole==='Agency'?'selected':'') + `>Agency (Tier 40)</option>
                        <option value="BD" ` + (currentRole==='BD'?'selected':'') + `>BD - Business Development (Tier 50)</option>
                        <option value="Manager" ` + (currentRole==='Manager'?'selected':'') + `>Manager (Tier 60)</option>
                        <option value="Admin" ` + (currentRole==='Admin'?'selected':'') + `>Admin (Tier 80)</option>
                        <option value="Super Admin" ` + (currentRole==='Super Admin'?'selected':'') + `>Super Admin (Tier 100)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Assigned Area / Territory (Optional)</label>
                    <input type="text" id="modalAssignedArea" class="form-input" placeholder="e.g. PK-Zone-1 or Room ID">
                </div>
                <div class="form-group">
                    <label class="form-label">Administrative Notes</label>
                    <input type="text" id="modalRoleNotes" class="form-input" placeholder="Reason or assignment mandate">
                </div>
                <button class="btn-primary" onclick="submitRoleChange('` + userId + `')">Confirm & Apply Role</button>
            `;
            document.getElementById('actionModal').style.display = 'flex';
        }

        async function submitRoleChange(userId) {
            const role = document.getElementById('modalRoleSelect').value;
            const assignedArea = document.getElementById('modalAssignedArea').value;
            const notes = document.getElementById('modalRoleNotes').value;

            try {
                const res = await fetch('/api/admin/users/role', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({
                        targetUserId: userId,
                        role: role,
                        assignedArea: assignedArea,
                        notes: notes
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Role updated to ' + role + ' successfully', 'success');
                    closeModal();
                    refreshCurrentTab();
                } else {
                    showToast(data.message || 'Failed to update role', 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        function openBalanceModal(userId, username) {
            const modal = document.getElementById('modalBoxContent');
            modal.innerHTML = `
                <div class="modal-header">
                    <div class="modal-title">Wallet Adjustment for ` + username + `</div>
                    <button class="close-btn" onclick="closeModal()">✕</button>
                </div>
                <div class="form-group">
                    <label class="form-label">Target User ID</label>
                    <input type="text" class="form-input" value="` + userId + `" disabled>
                </div>
                <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                    <div class="form-group">
                        <label class="form-label">Coins Delta (+ / -)</label>
                        <input type="number" id="modalCoinsDelta" class="form-input" value="0" placeholder="e.g. 5000 or -1000">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Diamonds Delta (+ / -)</label>
                        <input type="number" id="modalDiamondsDelta" class="form-input" value="0" placeholder="e.g. 100">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Administrative Reason <span style="color:var(--danger);">*</span></label>
                    <input type="text" id="modalBalanceReason" class="form-input" placeholder="e.g. Event Reward, Refund, Quota Allocation" required>
                </div>
                <button class="btn-primary" onclick="submitBalanceChange('` + userId + `')">Authorize & Apply Adjustment</button>
            `;
            document.getElementById('actionModal').style.display = 'flex';
        }

        async function submitBalanceChange(userId) {
            const coins = parseInt(document.getElementById('modalCoinsDelta').value) || 0;
            const diamonds = parseInt(document.getElementById('modalDiamondsDelta').value) || 0;
            const reason = document.getElementById('modalBalanceReason').value.trim();

            if (!reason) {
                return showToast('Reason is required for audit logs', 'error');
            }

            try {
                const res = await fetch('/api/admin/users/balance', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({
                        targetUserId: userId,
                        deltaCoins: coins,
                        deltaDiamonds: diamonds,
                        reason: reason
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Balance updated successfully!', 'success');
                    closeModal();
                    refreshCurrentTab();
                } else {
                    showToast(data.message || 'Balance update failed', 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function toggleUserBan(userId, isBanned) {
            const reason = prompt(isBanned ? 'Enter reason for account suspension:' : 'Enter reason for unbanning account:');
            if (reason === null) return;

            try {
                const res = await fetch('/api/admin/users/ban', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({
                        targetUserId: userId,
                        isBanned: isBanned,
                        reason: reason || (isBanned ? 'Administrative suspension' : 'Suspension lifted')
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast(isBanned ? 'Account suspended' : 'Account unbanned', 'success');
                    refreshCurrentTab();
                } else {
                    showToast(data.message || 'Action failed', 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        function openPromoteSuperAdminModal() {
            const modal = document.getElementById('modalBoxContent');
            modal.innerHTML = `
                <div class="modal-header">
                    <div class="modal-title" style="color:var(--gold);">👑 Grant Super Admin Access</div>
                    <button class="close-btn" onclick="closeModal()">✕</button>
                </div>
                <p style="font-size:12.5px; color:var(--text-secondary); margin-bottom:16px;">
                    Granting Super Admin provides root administrative control over the entire platform.
                </p>
                <div class="form-group">
                    <label class="form-label">Existing User ID or Email</label>
                    <input type="text" id="promoteSuperAdminId" class="form-input" placeholder="e.g. 100002">
                </div>
                <div class="form-group">
                    <label class="form-label">Type "CONFIRM" to authorize root access</label>
                    <input type="text" id="promoteSuperAdminConfirm" class="form-input" placeholder="CONFIRM">
                </div>
                <button class="btn-primary" style="background:linear-gradient(135deg, #FFD700, #FFA000); color:#000;" onclick="submitPromoteSuperAdmin()">
                    Grant Super Admin Privileges
                </button>
            `;
            document.getElementById('actionModal').style.display = 'flex';
        }

        async function submitPromoteSuperAdmin() {
            const uid = document.getElementById('promoteSuperAdminId').value.trim();
            const conf = document.getElementById('promoteSuperAdminConfirm').value.trim();
            if (conf !== 'CONFIRM') {
                return showToast('Please type CONFIRM exactly to proceed', 'error');
            }
            if (!uid) return showToast('User ID is required', 'error');

            try {
                const res = await fetch('/api/admin/users/role', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({
                        targetUserId: uid,
                        role: 'Super Admin',
                        notes: 'Promoted to Super Admin with root confirmation'
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Super Admin granted successfully', 'success');
                    closeModal();
                    refreshCurrentTab();
                } else {
                    showToast(data.message, 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function revokeSuperAdminConfirm(userId, username) {
            const conf = prompt('Revoke Super Admin privileges from ' + username + ' (' + userId + ')?\nType "REVOKE" to confirm:');
            if (conf !== 'REVOKE') return;

            try {
                const res = await fetch('/api/admin/users/role', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({
                        targetUserId: userId,
                        role: 'User',
                        notes: 'Super Admin privileges revoked'
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Super Admin privileges revoked', 'success');
                    refreshCurrentTab();
                } else {
                    showToast(data.message, 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function moderateRoomAction(roomId, action) {
            try {
                const res = await fetch('/api/admin/rooms/action', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({ roomId, action })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Room action ' + action + ' succeeded', 'success');
                    refreshCurrentTab();
                } else {
                    showToast(data.message, 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        function openAnnouncementModal(roomId, currentAnnounce) {
            const modal = document.getElementById('modalBoxContent');
            modal.innerHTML = `
                <div class="modal-header">
                    <div class="modal-title">Edit Room Announcement</div>
                    <button class="close-btn" onclick="closeModal()">✕</button>
                </div>
                <div class="form-group">
                    <label class="form-label">Announcement Text</label>
                    <textarea id="modalRoomAnnouncement" class="form-textarea" rows="3">` + currentAnnounce + `</textarea>
                </div>
                <button class="btn-primary" onclick="submitRoomAnnouncement('` + roomId + `')">Save Announcement</button>
            `;
            document.getElementById('actionModal').style.display = 'flex';
        }

        async function submitRoomAnnouncement(roomId) {
            const text = document.getElementById('modalRoomAnnouncement').value;
            try {
                const res = await fetch('/api/admin/rooms/action', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({ roomId, action: 'UPDATE_ANNOUNCEMENT', payload: text })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Announcement updated', 'success');
                    closeModal();
                    refreshCurrentTab();
                } else {
                    showToast(data.message, 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function resolveReportAction(reportId, status) {
            const notes = prompt('Enter resolution notes / action taken:');
            if (notes === null) return;

            try {
                const res = await fetch('/api/admin/reports/resolve', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentSession.token
                    },
                    body: JSON.stringify({ reportId, status, notes: notes || 'Resolved by Administrator' })
                });
                const data = await res.json();
                if (data.success) {
                    showToast('Report marked as ' + status, 'success');
                    refreshCurrentTab();
                } else {
                    showToast(data.message, 'error');
                }
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function saveGeneralConfigs() {
            const updates = [
                { key: 'app_name', value: document.getElementById('cfg_app_name').value, category: 'branding' },
                { key: 'welcome_message', value: document.getElementById('cfg_welcome_message').value, category: 'general' },
                { key: 'announcement_banner', value: document.getElementById('cfg_announcement_banner').value, category: 'general' },
                { key: 'support_email', value: document.getElementById('cfg_support_email').value, category: 'general' },
                { key: 'support_whatsapp', value: document.getElementById('cfg_support_whatsapp').value, category: 'general' }
            ];

            for (const u of updates) {
                await fetch('/api/admin/config', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + currentSession.token },
                    body: JSON.stringify(u)
                });
            }
            showToast('General settings saved successfully!', 'success');
        }

        async function saveFeatureSwitches() {
            const updates = [
                { key: 'feature_voice_rooms', value: document.getElementById('cfg_feature_voice_rooms').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_live_chat', value: document.getElementById('cfg_feature_live_chat').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_minigames', value: document.getElementById('cfg_feature_minigames').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_moments', value: document.getElementById('cfg_feature_moments').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_coin_reseller', value: document.getElementById('cfg_feature_coin_reseller').checked ? 'true' : 'false', category: 'features' },
                { key: 'feature_maintenance_mode', value: document.getElementById('cfg_feature_maintenance_mode').checked ? 'true' : 'false', category: 'features' }
            ];

            for (const u of updates) {
                await fetch('/api/admin/config', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + currentSession.token },
                    body: JSON.stringify(u)
                });
            }
            showToast('Feature switches updated successfully!', 'success');
        }

        async function saveBrandingConfigs() {
            const updates = [
                { key: 'theme_mode', value: document.getElementById('cfg_theme_mode').value, category: 'branding' },
                { key: 'primary_accent_color', value: document.getElementById('cfg_primary_accent_color').value, category: 'branding' },
                { key: 'secondary_accent_color', value: document.getElementById('cfg_secondary_accent_color').value, category: 'branding' },
                { key: 'app_logo', value: document.getElementById('cfg_app_logo').value, category: 'branding' },
                { key: 'splash_title', value: document.getElementById('cfg_splash_title').value, category: 'branding' },
                { key: 'splash_subtitle', value: document.getElementById('cfg_splash_subtitle').value, category: 'branding' },
                { key: 'navigation_style', value: document.getElementById('cfg_navigation_style').value, category: 'branding' }
            ];

            for (const u of updates) {
                await fetch('/api/admin/config', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + currentSession.token },
                    body: JSON.stringify(u)
                });
            }
            showToast('Branding configurations updated successfully!', 'success');
        }

        function escapeHtml(str) {
            return (str || '').replace(/'/g, "\\'").replace(/"/g, '&quot;');
        }
    </script>
</body>
</html>
    """
}
