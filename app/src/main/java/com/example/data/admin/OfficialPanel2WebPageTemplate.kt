package com.example.data.admin

object OfficialPanel2WebPageTemplate {

    fun getHtml(): String {
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Official Panel 2 | Executive Administration</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-base: #060B14;
            --bg-surface: rgba(12, 20, 36, 0.85);
            --bg-card: rgba(16, 27, 48, 0.65);
            --bg-input: #0A1220;
            --border-color: rgba(59, 130, 246, 0.22);
            --border-glow: rgba(59, 130, 246, 0.45);
            --primary: #3B82F6;
            --primary-hover: #2563EB;
            --cyan: #06B6D4;
            --emerald: #10B981;
            --gold: #F59E0B;
            --danger: #EF4444;
            --text-primary: #F8FAFC;
            --text-secondary: #94A3B8;
            --text-muted: #64748B;
            --font-main: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
            --font-mono: 'JetBrains Mono', monospace;
            --radius-sm: 8px;
            --radius-md: 14px;
            --radius-lg: 20px;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            background-color: var(--bg-base);
            background-image: 
                radial-gradient(circle at 15% 15%, rgba(59, 130, 246, 0.12) 0%, transparent 45%),
                radial-gradient(circle at 85% 85%, rgba(6, 182, 212, 0.1) 0%, transparent 45%),
                linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
                linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
            background-size: 100% 100%, 100% 100%, 40px 40px, 40px 40px;
            color: var(--text-primary);
            font-family: var(--font-main);
            min-height: 100vh;
            line-height: 1.5;
            -webkit-font-smoothing: antialiased;
        }

        /* Glass Utility */
        .glass-panel {
            background: var(--bg-surface);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            backdrop-filter: blur(16px);
            box-shadow: 0 16px 40px rgba(0, 0, 0, 0.5);
        }

        /* Authentication Screen */
        .auth-screen {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 24px;
        }

        .auth-card {
            width: 100%;
            max-width: 440px;
            padding: 38px 32px;
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            background: rgba(12, 20, 36, 0.92);
            box-shadow: 0 24px 60px rgba(0, 0, 0, 0.7), 0 0 30px rgba(59, 130, 246, 0.15);
        }

        .auth-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 5px 12px;
            background: rgba(59, 130, 246, 0.14);
            border: 1px solid rgba(59, 130, 246, 0.35);
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
            color: var(--cyan);
            letter-spacing: 0.8px;
            text-transform: uppercase;
            margin-bottom: 16px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-label {
            display: block;
            font-size: 12.5px;
            font-weight: 600;
            color: var(--text-secondary);
            margin-bottom: 7px;
            letter-spacing: 0.2px;
        }

        .form-input {
            width: 100%;
            padding: 12px 15px;
            background: var(--bg-input);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: var(--radius-sm);
            color: var(--text-primary);
            font-size: 14px;
            font-family: inherit;
            transition: all 0.2s ease;
        }

        .form-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.25);
            background: #0d182b;
        }

        .btn-primary {
            width: 100%;
            padding: 13px 20px;
            background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: var(--radius-sm);
            color: white;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.2s ease;
            box-shadow: 0 8px 20px rgba(37, 99, 235, 0.35);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(37, 99, 235, 0.45);
        }

        .btn-primary:active { transform: translateY(0); }

        .btn-secondary {
            padding: 9px 16px;
            background: rgba(255, 255, 255, 0.06);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: var(--radius-sm);
            color: var(--text-primary);
            font-size: 12.5px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.12);
            border-color: rgba(255, 255, 255, 0.2);
        }

        .btn-danger {
            padding: 6px 12px;
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.4);
            border-radius: 6px;
            color: #FCA5A5;
            font-size: 11.5px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-danger:hover {
            background: rgba(239, 68, 68, 0.3);
            color: #FFFFFF;
        }

        /* Main Application Layout */
        #panelApp {
            display: none;
            min-height: 100vh;
        }

        /* Top Navigation Header */
        .topbar {
            position: sticky;
            top: 0;
            z-index: 100;
            background: rgba(8, 14, 26, 0.94);
            border-bottom: 1px solid var(--border-color);
            backdrop-filter: blur(14px);
            padding: 14px 28px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
        }

        .brand-container {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .brand-logo-badge {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            background: linear-gradient(135deg, #1E40AF 0%, #3B82F6 100%);
            border: 1px solid rgba(255, 255, 255, 0.25);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
        }

        .user-status-card {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .admin-pill {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 6px 14px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 999px;
            font-size: 12px;
        }

        /* Navigation Tabs */
        .nav-tabs-bar {
            background: rgba(10, 18, 32, 0.7);
            border-bottom: 1px solid rgba(255, 255, 255, 0.08);
            padding: 8px 28px;
            display: flex;
            gap: 10px;
            overflow-x: auto;
        }

        .tab-btn {
            padding: 9px 18px;
            border-radius: var(--radius-sm);
            border: 1px solid transparent;
            background: transparent;
            color: var(--text-secondary);
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 7px;
            transition: all 0.2s;
            white-space: nowrap;
        }

        .tab-btn:hover {
            color: var(--text-primary);
            background: rgba(255, 255, 255, 0.04);
        }

        .tab-btn.active {
            color: #FFFFFF;
            background: rgba(59, 130, 246, 0.16);
            border-color: rgba(59, 130, 246, 0.4);
            box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
        }

        /* Content Container */
        .content-container {
            max-width: 1340px;
            margin: 0 auto;
            padding: 26px 28px 60px;
        }

        .tab-pane {
            display: none;
        }

        .tab-pane.active {
            display: block;
            animation: fadeIn 0.25s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(6px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* Frames Grid */
        .frames-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 16px;
            margin-top: 18px;
        }

        .frame-card {
            background: var(--bg-card);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: var(--radius-md);
            padding: 18px;
            display: flex;
            align-items: center;
            gap: 14px;
            transition: all 0.2s ease;
            position: relative;
            overflow: hidden;
        }

        .frame-card:hover {
            border-color: var(--border-glow);
            transform: translateY(-2px);
            box-shadow: 0 10px 24px rgba(0, 0, 0, 0.4);
            background: rgba(22, 36, 62, 0.7);
        }

        .frame-card.selected {
            border-color: var(--cyan);
            box-shadow: 0 0 16px rgba(6, 182, 212, 0.35);
        }

        .frame-badge-avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            flex-shrink: 0;
            border: 2px solid;
            background: rgba(0, 0, 0, 0.4);
        }

        /* Table */
        .table-responsive {
            width: 100%;
            overflow-x: auto;
            border-radius: var(--radius-md);
            border: 1px solid rgba(255, 255, 255, 0.08);
            background: var(--bg-card);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 13px;
        }

        th {
            background: rgba(12, 20, 36, 0.95);
            padding: 13px 16px;
            color: var(--text-secondary);
            font-weight: 700;
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 0.6px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        td {
            padding: 14px 16px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
            color: var(--text-primary);
        }

        tr:hover td {
            background: rgba(255, 255, 255, 0.02);
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 3px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .status-active {
            background: rgba(16, 185, 129, 0.15);
            color: #34D399;
            border: 1px solid rgba(16, 185, 129, 0.35);
        }

        .status-expired {
            background: rgba(100, 116, 139, 0.2);
            color: #94A3B8;
            border: 1px solid rgba(100, 116, 139, 0.3);
        }

        .status-revoked {
            background: rgba(239, 68, 68, 0.15);
            color: #F87171;
            border: 1px solid rgba(239, 68, 68, 0.35);
        }

        /* Toast notifications */
        .toast-box {
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 10000;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .toast-item {
            padding: 12px 18px;
            border-radius: var(--radius-sm);
            font-size: 13px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.6);
            animation: slideIn 0.3s ease;
        }

        .toast-success { background: #064E3B; color: #A7F3D0; border: 1px solid #059669; }
        .toast-error { background: #7F1D1D; color: #FECACA; border: 1px solid #DC2626; }
        .toast-info { background: #1E3A8A; color: #BFDBFE; border: 1px solid #3B82F6; }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @media (max-width: 768px) {
            .topbar { padding: 12px 16px; flex-wrap: wrap; }
            .content-container { padding: 18px 16px; }
            .nav-tabs-bar { padding: 8px 16px; }
        }
    </style>
</head>
<body>

    <!-- 1. OFFICIAL PANEL 2 LOGIN SCREEN -->
    <div id="authScreen" class="auth-screen">
        <div class="auth-card">
            <div class="auth-badge">
                <span>🛡️</span> OFFICIAL PANEL 2 GATEWAY
            </div>
            
            <h1 style="font-size: 23px; font-weight: 800; letter-spacing: -0.5px; margin-bottom: 6px; color: #FFFFFF;">
                Official Panel 2
            </h1>
            <p style="font-size: 13px; color: var(--text-secondary); margin-bottom: 24px;">
                Enter authorized credentials to access Official Panel 2.
            </p>

            <div id="loginAlertBox" style="display: none; padding: 11px 14px; background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.4); border-radius: var(--radius-sm); color: #FCA5A5; font-size: 12.5px; font-weight: 600; margin-bottom: 18px;"></div>

            <!-- Login Form strictly with: Username: Maz, ID: 41387, Password: 30484 -->
            <form id="panel2LoginForm" onsubmit="handlePanel2Login(event)" autocomplete="off">
                <div class="form-group">
                    <label class="form-label" for="loginUsername">Username</label>
                    <input type="text" id="loginUsername" class="form-input" placeholder="Enter Username (Maz)" required autocomplete="off">
                </div>

                <div class="form-group">
                    <label class="form-label" for="loginId">ID</label>
                    <input type="text" id="loginId" class="form-input" placeholder="Enter ID (41387)" required autocomplete="off">
                </div>

                <div class="form-group">
                    <label class="form-label" for="loginPassword">Password</label>
                    <input type="password" id="loginPassword" class="form-input" placeholder="Enter Password" required autocomplete="off">
                </div>

                <button type="submit" id="btnSubmitLogin" class="btn-primary" style="margin-top: 10px;">
                    <span>🔐 Sign In to Official Panel 2</span>
                </button>
            </form>

            <div style="margin-top: 24px; padding-top: 18px; border-top: 1px solid rgba(255,255,255,0.07); text-align: center; font-size: 11.5px; color: var(--text-muted); display: flex; align-items: center; justify-content: center; gap: 6px;">
                <span>🔒</span> Dedicated Administrative Authentication Gateway
            </div>
        </div>
    </div>

    <!-- 2. OFFICIAL PANEL 2 MAIN APPLICATION INTERFACE -->
    <div id="panelApp">
        <!-- Header -->
        <header class="topbar">
            <div class="brand-container">
                <div class="brand-logo-badge">👑</div>
                <div>
                    <h2 style="font-size: 16px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                        OFFICIAL PANEL 2
                        <span style="font-size: 10px; background: rgba(59, 130, 246, 0.25); color: #60A5FA; border: 1px solid rgba(59, 130, 246, 0.4); padding: 2px 7px; border-radius: 4px; font-weight: 700;">ACTIVE</span>
                    </h2>
                    <p style="font-size: 11.5px; color: var(--cyan); font-weight: 600;">Executive Frame Management Portal</p>
                </div>
            </div>

            <div class="user-status-card">
                <div class="admin-pill">
                    <span style="width: 8px; height: 8px; border-radius: 50%; background: #10B981; box-shadow: 0 0 8px #10B981;"></span>
                    <span style="color: var(--text-secondary);">Admin:</span>
                    <strong style="color: #FFFFFF;" id="displayAdminName">Maz</strong>
                    <span style="color: var(--gold); font-family: var(--font-mono); font-size: 11px;">(ID: <span id="displayAdminId">41387</span>)</span>
                </div>
                <button class="btn-secondary" onclick="handlePanel2Logout()" style="padding: 7px 12px; font-size: 12px;">
                    🚪 Sign Out
                </button>
            </div>
        </header>

        <!-- Navigation Tabs -->
        <nav class="nav-tabs-bar">
            <button class="tab-btn active" onclick="switchPanelTab('frames')">
                <span>👑</span> Frame Management & Sending
            </button>
            <button class="tab-btn" onclick="switchPanelTab('history')">
                <span>📜</span> Frame Sending History
            </button>
            <button class="tab-btn" onclick="switchPanelTab('catalog')">
                <span>✨</span> Official Frame Definitions
            </button>
            <button class="tab-btn" onclick="switchPanelTab('overview')">
                <span>📊</span> Panel 2 Overview
            </button>
        </nav>

        <!-- Main Body -->
        <main class="content-container">

            <!-- TAB 1: FRAME MANAGEMENT & SENDING -->
            <section id="tab-frames" class="tab-pane active">
                <div class="glass-panel" style="padding: 28px; margin-bottom: 24px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 16px; margin-bottom: 22px; flex-wrap: wrap; gap: 12px;">
                        <div>
                            <h3 style="font-size: 20px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                                <span>👑</span> Send Official Frame
                            </h3>
                            <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                                Assign an important official frame directly to a specific User ID. The selected frame will only be sent to the entered User ID.
                            </p>
                        </div>
                        <div style="font-size: 11px; padding: 6px 14px; border-radius: 999px; background: rgba(59, 130, 246, 0.15); border: 1px solid rgba(59, 130, 246, 0.4); color: #93C5FD; font-weight: 700;">
                            OFFICIAL PANEL 2 DISPATCH SYSTEM
                        </div>
                    </div>

                    <!-- Send Frame Form -->
                    <form id="sendFrameForm" onsubmit="submitSendFrame(event)" autocomplete="off">
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; margin-bottom: 22px;">
                            
                            <!-- 1. Select Frame -->
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label" style="font-weight: 700; color: #FFFFFF; display: flex; justify-content: space-between;">
                                    <span>1. Select Frame *</span>
                                    <span style="font-size: 11px; color: var(--cyan);">Important Official Frames</span>
                                </label>
                                <select id="frameSelect" class="form-input" required onchange="onFrameSelectionChanged()" style="font-weight: 600;">
                                    <option value="" disabled selected>-- Select Official Frame ▼ --</option>
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
                                    <option value="frame_cs">CS</option>
                                    <option value="frame_cs_leader">CS Leader</option>
                                </select>
                            </div>

                            <!-- 2. Enter User ID -->
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label" style="font-weight: 700; color: #FFFFFF;">
                                    2. Enter User ID *
                                </label>
                                <input type="text" id="targetUserId" class="form-input" placeholder="e.g. 41387, usr_78912, or 10001" required oninput="onUserIdInputDebounced()">
                                <div id="conflictAlertBox" style="display: none; margin-top: 6px; font-size: 11.5px; padding: 6px 10px; border-radius: 6px; line-height: 1.4;"></div>
                            </div>

                            <!-- 3. Days Selection Option -->
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label" style="font-weight: 700; color: #FFFFFF;">
                                    3. Select Number of Days *
                                </label>
                                <select id="daysSelect" class="form-input" required onchange="updateLivePreview()" style="font-weight: 600;">
                                    <option value="" disabled selected>-- Select Duration ▼ --</option>
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

                        <!-- Live Visual Preview of Selected Frame -->
                        <div id="livePreviewContainer" style="background: rgba(10, 18, 34, 0.6); border: 1px solid rgba(59, 130, 246, 0.25); border-radius: var(--radius-md); padding: 18px 24px; margin-bottom: 22px; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 16px;">
                            <div style="display: flex; align-items: center; gap: 16px;">
                                <div id="previewAvatar" style="width: 56px; height: 56px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 26px; border: 2px solid #FFD700; background: radial-gradient(circle, rgba(255,215,0,0.25) 0%, rgba(0,0,0,0.7) 100%); box-shadow: 0 0 16px rgba(255,215,0,0.3);">
                                    🛡️
                                </div>
                                <div>
                                    <div style="display: flex; align-items: center; gap: 8px;">
                                        <h4 id="previewTitle" style="font-size: 16px; font-weight: 800; color: #FFFFFF;">Select an Official Frame</h4>
                                        <span id="previewTag" style="font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 4px; background: rgba(59,130,246,0.25); color: #60A5FA; border: 1px solid #3B82F6;">OFFICIAL</span>
                                    </div>
                                    <p id="previewDesc" style="font-size: 12.5px; color: var(--text-secondary); margin-top: 3px;">
                                        Authorized Official Panel 2 Frame Dispatch System.
                                    </p>
                                </div>
                            </div>

                            <div style="text-align: right;">
                                <div style="font-size: 11px; color: var(--text-muted); text-transform: uppercase;">Duration Preview</div>
                                <div id="previewDuration" style="font-size: 13px; font-weight: 700; color: var(--emerald);">
                                    Select days
                                </div>
                            </div>
                        </div>

                        <!-- Action Submit -->
                        <div style="display: flex; justify-content: flex-end;">
                            <button type="submit" id="btnSendFrame" class="btn-primary" style="padding: 13px 36px; width: auto; font-size: 14px; min-width: 220px;">
                                <span>👑 Send Frame to User ID</span>
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Recent Dispatches on this screen -->
                <div class="glass-panel" style="padding: 24px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 10px;">
                        <h4 style="font-size: 16px; font-weight: 700; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                            <span>📜</span> Recent Dispatched Frames
                        </h4>
                        <button class="btn-secondary" onclick="loadFrameHistory()" style="font-size: 12px;">
                            🔄 Refresh List
                        </button>
                    </div>
                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Target User ID</th>
                                    <th>User Name</th>
                                    <th>Official Frame</th>
                                    <th>Duration</th>
                                    <th>Dispatched By</th>
                                    <th>Expiry Date</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody id="quickHistoryTableBody">
                                <tr><td colspan="8" style="text-align: center; padding: 24px; color: var(--text-muted);">Loading dispatched frames...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>

            <!-- TAB 2: FRAME SENDING HISTORY -->
            <section id="tab-history" class="tab-pane">
                <div class="glass-panel" style="padding: 26px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 14px;">
                        <div>
                            <h3 style="font-size: 19px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                                <span>📜</span> Frame Sending History & Audit Log
                            </h3>
                            <p style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">
                                Full historical log of all official frames dispatched, duration tracking, and auto-removal status.
                            </p>
                        </div>
                        <div style="display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
                            <input type="text" id="historySearchInput" class="form-input" placeholder="Search User ID or Frame..." style="max-width: 240px; padding: 8px 12px; font-size: 12.5px;" oninput="filterHistoryDebounced()">
                            <button class="btn-secondary" onclick="loadFrameHistory()" style="font-size: 12.5px;">
                                🔄 Refresh
                            </button>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Target User ID</th>
                                    <th>User Name</th>
                                    <th>Frame Name & Category</th>
                                    <th>Days</th>
                                    <th>Send Date</th>
                                    <th>Expiry Date</th>
                                    <th>Remaining Days</th>
                                    <th>Admin Dispatcher</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="fullHistoryTableBody">
                                <tr><td colspan="10" style="text-align: center; padding: 24px; color: var(--text-muted);">Loading frame sending history...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>

            <!-- TAB 3: OFFICIAL FRAME DEFINITIONS (CATALOG) -->
            <section id="tab-catalog" class="tab-pane">
                <div class="glass-panel" style="padding: 26px;">
                    <div style="margin-bottom: 20px; border-bottom: 1px solid var(--border-color); padding-bottom: 14px;">
                        <h3 style="font-size: 19px; font-weight: 800; color: #FFFFFF; display: flex; align-items: center; gap: 8px;">
                            <span>✨</span> Important Official Frames Catalog
                        </h3>
                        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                            All 13 recognized official frame categories authorized for Official Panel 2. Click any frame to pre-fill the sending form.
                        </p>
                    </div>

                    <div id="framesCatalogGrid" class="frames-grid">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>
            </section>

            <!-- TAB 4: PANEL 2 OVERVIEW -->
            <section id="tab-overview" class="tab-pane">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 18px; margin-bottom: 24px;">
                    <div class="glass-panel" style="padding: 22px;">
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Authorized Administrator</div>
                        <div style="font-size: 22px; font-weight: 800; color: #FFFFFF; margin-top: 6px;">Maz</div>
                        <div style="font-size: 12px; color: var(--cyan); margin-top: 2px;">ID: 41387 • Super Admin</div>
                    </div>

                    <div class="glass-panel" style="padding: 22px;">
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Official Frame Categories</div>
                        <div style="font-size: 22px; font-weight: 800; color: var(--gold); margin-top: 6px;">13 Categories</div>
                        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">Official, Admin, BD, Agency, Host, etc.</div>
                    </div>

                    <div class="glass-panel" style="padding: 22px;">
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Active Frame Assignments</div>
                        <div id="statsActiveFrames" style="font-size: 22px; font-weight: 800; color: var(--emerald); margin-top: 6px;">--</div>
                        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">Equipped to users across app</div>
                    </div>

                    <div class="glass-panel" style="padding: 22px;">
                        <div style="font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">System Security Status</div>
                        <div style="font-size: 22px; font-weight: 800; color: #60A5FA; margin-top: 6px;">Protected</div>
                        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">Conflict checking & auto-expiry active</div>
                    </div>
                </div>

                <div class="glass-panel" style="padding: 26px;">
                    <h4 style="font-size: 16px; font-weight: 700; color: #FFFFFF; margin-bottom: 12px;">
                        🛡️ Official Panel 2 Security Policy
                    </h4>
                    <ul style="font-size: 13px; color: var(--text-secondary); line-height: 1.8; padding-left: 20px;">
                        <li>Only authorized user <strong>Maz</strong> (ID: <strong>41387</strong>) with password <strong>30484</strong> can authenticate into Official Panel 2.</li>
                        <li>The selected frame is dispatched strictly and exclusively to the specified target User ID number.</li>
                        <li>Automated conflict check prevents duplicate overlapping active frames from being assigned to the same user.</li>
                        <li>Durations are precisely enforced: once the selected number of days expires, the system automatically marks the frame as Expired and unequips it from the user's avatar.</li>
                        <li>All actions taken in Official Panel 2 are audited in real time with timestamp, IP, and administrator identifier.</li>
                    </ul>
                </div>
            </section>

        </main>
    </div>

    <!-- Toast Container -->
    <div id="toastContainer" class="toast-box"></div>

    <script>
        // Official Panel 2 State
        let currentPanel2Token = sessionStorage.getItem('official_panel_2_token') || '';
        let officialFrameDefs = [];
        let frameHistoryCache = [];
        let conflictDebounceTimer = null;
        let searchDebounceTimer = null;

        // Frame Category Definitions Cache
        const FRAME_METADATA = {
            'frame_official': { name: 'Official', badge: 'OFFICIAL', icon: '🛡️', color: '#FFD700', desc: 'Official Staff Identity Frame' },
            'frame_manager': { name: 'Manager', badge: 'MANAGER', icon: '👑', color: '#9C27B0', desc: 'Platform Management Authority Frame' },
            'frame_super_admin': { name: 'Super Admin', badge: 'SUPER ADMIN', icon: '🔥', color: '#FF1744', desc: 'Supreme Administrative Crest Frame' },
            'frame_admin': { name: 'Admin', badge: 'ADMIN', icon: '⚔️', color: '#00E5FF', desc: 'Official Administrator Frame' },
            'frame_admin_leader': { name: 'Admin Leader', badge: 'ADMIN LEADER', icon: '🌟', color: '#FF9100', desc: 'Admin Leadership Laurel Frame' },
            'frame_bd': { name: 'BD', badge: 'BD', icon: '🐉', color: '#00C853', desc: 'Business Development Executive Frame' },
            'frame_bd_leader': { name: 'BD Leader', badge: 'BD LEADER', icon: '🏆', color: '#00E676', desc: 'Head of Business Development Frame' },
            'frame_agency': { name: 'Agency', badge: 'AGENCY', icon: '🏢', color: '#7C4DFF', desc: 'Official Certified Agency Frame' },
            'frame_agency_leader': { name: 'Agency Leader', badge: 'AGENCY LEADER', icon: '💎', color: '#B388FF', desc: 'Premier Agency Director Frame' },
            'frame_host': { name: 'Host', badge: 'HOST', icon: '🎙️', color: '#FF2A85', desc: 'Official Star Audio Host Frame' },
            'frame_coin_reseller': { name: 'Coin Reseller', badge: 'COIN RESELLER', icon: '🪙', color: '#FFC107', desc: 'Certified Coin Merchant Frame' },
            'frame_super_coin_reseller': { name: 'Super Coin Reseller', badge: 'SUPER COIN RESELLER', icon: '💰', color: '#FFD700', desc: 'Master Gold Distributor Frame' },
            'frame_cs': { name: 'CS', badge: 'CS', icon: '⚡', color: '#00B0FF', desc: 'Customer Support Official Frame' },
            'frame_cs_leader': { name: 'CS Leader', badge: 'CS LEADER', icon: '💠', color: '#2979FF', desc: 'Customer Support Leadership Frame' }
        };

        // Initialize on DOM ready
        document.addEventListener('DOMContentLoaded', () => {
            if (currentPanel2Token) {
                verifySessionAndOpenApp();
            } else {
                showAuthScreen();
            }
        });

        function showAuthScreen() {
            document.getElementById('authScreen').style.display = 'flex';
            document.getElementById('panelApp').style.display = 'none';
        }

        function showAppScreen() {
            document.getElementById('authScreen').style.display = 'none';
            document.getElementById('panelApp').style.display = 'block';
            loadFrameDefinitions();
            loadFrameHistory();
        }

        // Login Handler
        async function handlePanel2Login(e) {
            e.preventDefault();
            const alertBox = document.getElementById('loginAlertBox');
            alertBox.style.display = 'none';

            const username = document.getElementById('loginUsername').value.trim();
            const id = document.getElementById('loginId').value.trim();
            const password = document.getElementById('loginPassword').value.trim();
            const submitBtn = document.getElementById('btnSubmitLogin');

            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span>⏳ Verifying Credentials...</span>';

            try {
                const response = await fetch('/api/admin2/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, id, password })
                });

                const data = await response.json();

                if (response.ok && data.success) {
                    currentPanel2Token = data.token;
                    sessionStorage.setItem('official_panel_2_token', currentPanel2Token);
                    document.getElementById('displayAdminName').innerText = data.adminName || 'Maz';
                    document.getElementById('displayAdminId').innerText = data.adminId || '41387';
                    showToast('Welcome to Official Panel 2!', 'success');
                    showAppScreen();
                } else {
                    alertBox.innerText = data.message || 'Invalid Username, ID, or Password.';
                    alertBox.style.display = 'block';
                    showToast(data.message || 'Authentication failed', 'error');
                }
            } catch (err) {
                alertBox.innerText = 'Unable to reach server. Please check connection.';
                alertBox.style.display = 'block';
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<span>🔐 Sign In to Official Panel 2</span>';
            }
        }

        async function verifySessionAndOpenApp() {
            try {
                const res = await fetch('/api/admin2/info', {
                    headers: { 'Authorization': 'Bearer ' + currentPanel2Token }
                });
                if (res.ok) {
                    const data = await res.json();
                    document.getElementById('displayAdminName').innerText = data.adminName || 'Maz';
                    document.getElementById('displayAdminId').innerText = data.adminId || '41387';
                    showAppScreen();
                } else {
                    sessionStorage.removeItem('official_panel_2_token');
                    currentPanel2Token = '';
                    showAuthScreen();
                }
            } catch (e) {
                showAuthScreen();
            }
        }

        async function handlePanel2Logout() {
            try {
                await fetch('/api/admin2/logout', {
                    method: 'POST',
                    headers: { 'Authorization': 'Bearer ' + currentPanel2Token }
                });
            } catch (_) {}
            sessionStorage.removeItem('official_panel_2_token');
            currentPanel2Token = '';
            showToast('Logged out of Official Panel 2', 'info');
            showAuthScreen();
        }

        // Navigation Tabs
        function switchPanelTab(tabId) {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));

            const targetPane = document.getElementById('tab-' + tabId);
            if (targetPane) targetPane.classList.add('active');

            const btn = Array.from(document.querySelectorAll('.tab-btn')).find(b => b.getAttribute('onclick')?.includes(tabId));
            if (btn) btn.classList.add('active');

            if (tabId === 'history') loadFrameHistory();
            if (tabId === 'catalog') renderCatalogGrid();
        }

        // Load Frame Definitions from API
        async function loadFrameDefinitions() {
            try {
                const res = await fetch('/api/admin2/frames/definitions', {
                    headers: { 'Authorization': 'Bearer ' + currentPanel2Token }
                });
                if (res.ok) {
                    officialFrameDefs = await res.json();
                    renderCatalogGrid();
                }
            } catch (e) {
                console.error('Error loading frame definitions', e);
            }
        }

        function renderCatalogGrid() {
            const grid = document.getElementById('framesCatalogGrid');
            if (!grid) return;

            const defs = officialFrameDefs.length > 0 ? officialFrameDefs : Object.entries(FRAME_METADATA).map(([id, meta]) => ({
                id,
                name: meta.name,
                badgeLabel: meta.badge,
                iconEmoji: meta.icon,
                primaryColor: meta.color,
                description: meta.desc
            }));

            grid.innerHTML = defs.map(def => `
                <div class="frame-card" onclick="selectFrameFromCatalog('${'$'}{def.id}')">
                    <div class="frame-badge-avatar" style="border-color: ${'$'}{def.primaryColor || '#FFD700'}; color: ${'$'}{def.primaryColor || '#FFD700'};">
                        ${'$'}{def.iconEmoji || '🛡️'}
                    </div>
                    <div style="flex: 1; min-width: 0;">
                        <div style="display: flex; align-items: center; gap: 6px; margin-bottom: 3px;">
                            <strong style="color: #FFFFFF; font-size: 14px;">${'$'}{def.name}</strong>
                            <span style="font-size: 9.5px; padding: 1px 6px; border-radius: 4px; background: rgba(255,255,255,0.1); color: ${'$'}{def.primaryColor || '#FFD700'}; font-weight: 700;">
                                ${'$'}{def.badgeLabel || 'OFFICIAL'}
                            </span>
                        </div>
                        <p style="font-size: 11.5px; color: var(--text-secondary); line-height: 1.3; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                            ${'$'}{def.description || 'Important Official Frame'}
                        </p>
                    </div>
                    <button class="btn-secondary" style="padding: 5px 10px; font-size: 11px; flex-shrink: 0;">
                        Select
                    </button>
                </div>
            `).join('');
        }

        function selectFrameFromCatalog(frameId) {
            const select = document.getElementById('frameSelect');
            if (select) {
                select.value = frameId;
                onFrameSelectionChanged();
            }
            switchPanelTab('frames');
            showToast('Frame selected! Enter target User ID & duration.', 'info');
        }

        // Live Frame Preview Change
        function onFrameSelectionChanged() {
            updateLivePreview();
        }

        function updateLivePreview() {
            const frameSelect = document.getElementById('frameSelect');
            const daysSelect = document.getElementById('daysSelect');
            const frameId = frameSelect.value;
            const days = daysSelect.value;

            const meta = FRAME_METADATA[frameId] || { name: 'Select an Official Frame', badge: 'OFFICIAL', icon: '🛡️', color: '#FFD700', desc: 'Authorized Official Panel 2 Frame Dispatch System.' };

            const avatar = document.getElementById('previewAvatar');
            const title = document.getElementById('previewTitle');
            const tag = document.getElementById('previewTag');
            const desc = document.getElementById('previewDesc');
            const duration = document.getElementById('previewDuration');

            avatar.innerText = meta.icon;
            avatar.style.borderColor = meta.color;
            avatar.style.color = meta.color;
            avatar.style.boxShadow = '0 0 16px ' + meta.color + '44';

            title.innerText = meta.name;
            tag.innerText = meta.badge;
            tag.style.color = meta.color;
            tag.style.borderColor = meta.color;
            tag.style.background = meta.color + '22';
            desc.innerText = meta.desc;

            if (days) {
                duration.innerText = days + (days === '1' ? ' Day' : ' Days') + ' Validity';
                duration.style.color = 'var(--emerald)';
            } else {
                duration.innerText = 'Select days';
                duration.style.color = 'var(--text-muted)';
            }
        }

        // Debounced Conflict Check on User ID Input
        function onUserIdInputDebounced() {
            clearTimeout(conflictDebounceTimer);
            conflictDebounceTimer = setTimeout(checkConflict, 350);
        }

        async function checkConflict() {
            const userId = document.getElementById('targetUserId').value.trim();
            const alertBox = document.getElementById('conflictAlertBox');

            if (!userId) {
                alertBox.style.display = 'none';
                return;
            }

            try {
                const res = await fetch('/api/admin2/frames/check-conflict?userId=' + encodeURIComponent(userId), {
                    headers: { 'Authorization': 'Bearer ' + currentPanel2Token }
                });
                if (res.ok) {
                    const data = await res.json();
                    if (data.hasConflict) {
                        alertBox.style.display = 'block';
                        alertBox.style.background = 'rgba(239, 68, 68, 0.15)';
                        alertBox.style.border = '1px solid rgba(239, 68, 68, 0.4)';
                        alertBox.style.color = '#FCA5A5';
                        alertBox.innerHTML = '⚠️ <strong>Conflict Warning:</strong> ' + (data.message || 'User already has an active official frame.');
                    } else {
                        alertBox.style.display = 'block';
                        alertBox.style.background = 'rgba(16, 185, 129, 0.12)';
                        alertBox.style.border = '1px solid rgba(16, 185, 129, 0.35)';
                        alertBox.style.color = '#6EE7B7';
                        alertBox.innerHTML = '✓ <strong>Available:</strong> User ID is ready to receive this frame.';
                    }
                }
            } catch (_) {}
        }

        // Send Official Frame Submission
        async function submitSendFrame(e) {
            e.preventDefault();
            const frameId = document.getElementById('frameSelect').value;
            const userId = document.getElementById('targetUserId').value.trim();
            const days = parseInt(document.getElementById('daysSelect').value, 10);
            const btn = document.getElementById('btnSendFrame');

            if (!frameId || !userId || !days) {
                showToast('Please complete all required fields', 'error');
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span>⏳ Dispatching Frame...</span>';

            try {
                const res = await fetch('/api/admin2/frames/send', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentPanel2Token
                    },
                    body: JSON.stringify({ frameId, userId, days })
                });

                const data = await res.json();

                if (res.ok && data.success) {
                    showToast('Official frame dispatched successfully to User ID ' + userId + '!', 'success');
                    document.getElementById('targetUserId').value = '';
                    document.getElementById('conflictAlertBox').style.display = 'none';
                    loadFrameHistory();
                } else {
                    showToast(data.message || 'Failed to dispatch frame', 'error');
                }
            } catch (err) {
                showToast('Network error while dispatching frame', 'error');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>👑 Send Frame to User ID</span>';
            }
        }

        // Load History
        async function loadFrameHistory() {
            try {
                const res = await fetch('/api/admin2/frames/history', {
                    headers: { 'Authorization': 'Bearer ' + currentPanel2Token }
                });
                if (res.ok) {
                    frameHistoryCache = await res.json();
                    renderHistoryTables(frameHistoryCache);
                }
            } catch (e) {
                console.error('Error loading history', e);
            }
        }

        function renderHistoryTables(history) {
            const quickBody = document.getElementById('quickHistoryTableBody');
            const fullBody = document.getElementById('fullHistoryTableBody');

            if (!history || history.length === 0) {
                const emptyRow = '<tr><td colspan="10" style="text-align: center; padding: 24px; color: var(--text-muted);">No official frames have been sent yet.</td></tr>';
                if (quickBody) quickBody.innerHTML = emptyRow;
                if (fullBody) fullBody.innerHTML = emptyRow;
                updateStats(0);
                return;
            }

            const activeCount = history.filter(h => h.status === 'Active').length;
            updateStats(activeCount);

            // Quick table (first 5)
            if (quickBody) {
                quickBody.innerHTML = history.slice(0, 5).map(h => `
                    <tr>
                        <td><strong style="font-family: var(--font-mono); color: var(--cyan);">${'$'}{h.userId}</strong></td>
                        <td>${'$'}{h.userName || 'User ' + h.userId}</td>
                        <td>
                            <span style="display: flex; align-items: center; gap: 6px; font-weight: 700;">
                                <span>${'$'}{getFrameIcon(h.frameId)}</span>
                                ${'$'}{h.frameName}
                            </span>
                        </td>
                        <td>${'$'}{h.days} Days</td>
                        <td style="font-size: 12px; color: var(--text-muted);">${'$'}{h.adminName || 'Maz'}</td>
                        <td style="font-size: 12px;">${'$'}{h.expiryDateFormatted || '-'}</td>
                        <td>${'$'}{getStatusBadge(h.status, h.remainingDays)}</td>
                        <td>
                            ${'$'}{h.status === 'Active' ? `<button class="btn-danger" onclick="revokeAssignment('${'$'}{h.id}')">Revoke</button>` : '<span style="color: var(--text-muted); font-size: 11px;">-</span>'}
                        </td>
                    </tr>
                `).join('');
            }

            // Full table
            if (fullBody) {
                fullBody.innerHTML = history.map(h => `
                    <tr>
                        <td><strong style="font-family: var(--font-mono); color: var(--cyan);">${'$'}{h.userId}</strong></td>
                        <td>${'$'}{h.userName || 'User ' + h.userId}</td>
                        <td>
                            <span style="display: flex; align-items: center; gap: 6px; font-weight: 700;">
                                <span>${'$'}{getFrameIcon(h.frameId)}</span>
                                ${'$'}{h.frameName}
                            </span>
                        </td>
                        <td>${'$'}{h.days}</td>
                        <td style="font-size: 12px;">${'$'}{h.sendDateFormatted || '-'}</td>
                        <td style="font-size: 12px;">${'$'}{h.expiryDateFormatted || '-'}</td>
                        <td>
                            ${'$'}{h.status === 'Active' ? `<strong style="color: var(--emerald);">${'$'}{h.remainingDays}d</strong>` : '<span style="color: var(--text-muted);">0d</span>'}
                        </td>
                        <td style="font-size: 12px; color: var(--gold);">${'$'}{h.adminName || 'Maz (Official Panel 2)'}</td>
                        <td>${'$'}{getStatusBadge(h.status, h.remainingDays)}</td>
                        <td>
                            ${'$'}{h.status === 'Active' ? `<button class="btn-danger" onclick="revokeAssignment('${'$'}{h.id}')">Revoke</button>` : '<span style="color: var(--text-muted); font-size: 11px;">Closed</span>'}
                        </td>
                    </tr>
                `).join('');
            }
        }

        function getFrameIcon(frameId) {
            return FRAME_METADATA[frameId]?.icon || '🛡️';
        }

        function getStatusBadge(status, remainingDays) {
            if (status === 'Active') {
                return `<span class="status-badge status-active">● Active (${'$'}{remainingDays}d)</span>`;
            } else if (status === 'Revoked') {
                return `<span class="status-badge status-revoked">✕ Revoked</span>`;
            } else {
                return `<span class="status-badge status-expired">Expired</span>`;
            }
        }

        function updateStats(activeCount) {
            const el = document.getElementById('statsActiveFrames');
            if (el) el.innerText = activeCount;
        }

        function filterHistoryDebounced() {
            clearTimeout(searchDebounceTimer);
            searchDebounceTimer = setTimeout(() => {
                const query = document.getElementById('historySearchInput').value.trim().toLowerCase();
                if (!query) {
                    renderHistoryTables(frameHistoryCache);
                    return;
                }
                const filtered = frameHistoryCache.filter(h => 
                    (h.userId && h.userId.toLowerCase().includes(query)) ||
                    (h.userName && h.userName.toLowerCase().includes(query)) ||
                    (h.frameName && h.frameName.toLowerCase().includes(query))
                );
                renderHistoryTables(filtered);
            }, 250);
        }

        async function revokeAssignment(assignmentId) {
            if (!confirm('Are you sure you want to revoke this official frame from the user? It will be unequipped immediately.')) {
                return;
            }

            try {
                const res = await fetch('/api/admin2/frames/revoke', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + currentPanel2Token
                    },
                    body: JSON.stringify({ assignmentId })
                });

                const data = await res.json();
                if (res.ok && data.success) {
                    showToast('Official frame revoked successfully', 'success');
                    loadFrameHistory();
                } else {
                    showToast(data.message || 'Failed to revoke frame', 'error');
                }
            } catch (e) {
                showToast('Network error while revoking frame', 'error');
            }
        }

        function showToast(message, type = 'info') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = `toast-item toast-${'$'}{type}`;
            const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
            toast.innerHTML = `<span>${'$'}{icon}</span> <span>${'$'}{message}</span>`;
            container.appendChild(toast);
            setTimeout(() => {
                toast.style.opacity = '0';
                toast.style.transform = 'translateY(10px)';
                toast.style.transition = 'all 0.3s ease';
                setTimeout(() => toast.remove(), 300);
            }, 3500);
        }
    </script>
</body>
</html>"""
    }
}
