import fs from 'fs';
import path from 'path';

// 1. Ensure assets directory exists and copy compiled web dist
const distDir = path.resolve('dist');
const assetsDir = path.resolve('app/src/main/assets');

if (fs.existsSync(distDir)) {
  fs.mkdirSync(assetsDir, { recursive: true });
  fs.cpSync(distDir, assetsDir, { recursive: true });
  console.log('[Postbuild] Web assets copied to app/src/main/assets');
}

// 2. If local .build-outputs/app-debug.apk exists (AI Studio container), copy to build output path
const localApk = path.resolve('.build-outputs/app-debug.apk');
const targetApk = path.resolve('app/build/outputs/apk/debug/app-debug.apk');

if (fs.existsSync(localApk)) {
  fs.mkdirSync(path.dirname(targetApk), { recursive: true });
  fs.copyFileSync(localApk, targetApk);
  console.log('[Postbuild] Local APK mirrored for emulator preview');
}
