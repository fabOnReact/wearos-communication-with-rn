#!/usr/bin/env node
import { promises as fs, constants as fsConstants } from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const TEMPLATE_ROOT = path.resolve(__dirname, '..');

const TEMPLATE_ENTRIES = [
  'app',
  'build.gradle.kts',
  'gradle',
  'gradle.properties',
  'gradlew',
  'gradlew.bat',
  'settings.gradle.kts',
];

const EXCLUDED_DIRECTORY_NAMES = new Set(['build', '.gradle']);
const BINARY_EXTENSIONS = new Set([
  '.keystore',
  '.png',
  '.jpg',
  '.jpeg',
  '.webp',
  '.mp3',
  '.ico',
  '.jar',
  '.ttf',
  '.otf',
]);

function parseArgs(argv) {
  const args = argv.slice(2);
  const options = {};

  for (let i = 0; i < args.length; i += 1) {
    const arg = args[i];
    switch (arg) {
      case '--app-name':
      case '-n': {
        options.appName = args[++i];
        break;
      }
      case '--package-name':
      case '-p': {
        options.packageName = args[++i];
        break;
      }
      case '--output':
      case '-o': {
        options.output = args[++i];
        break;
      }
      case '--force': {
        options.force = true;
        break;
      }
      case '--help':
      case '-h': {
        options.help = true;
        break;
      }
      default: {
        if (arg.startsWith('-')) {
          throw new Error(`Unknown option: ${arg}`);
        }
        if (!options.appName) {
          options.appName = arg;
        } else if (!options.packageName) {
          options.packageName = arg;
        } else if (!options.output) {
          options.output = arg;
        } else {
          throw new Error(`Unexpected argument: ${arg}`);
        }
        break;
      }
    }
  }

  return options;
}

function printHelp() {
  console.log(`Usage: yarn create-wearos-app --app-name <name> --package-name <com.example.app> [--output <directory>]

Arguments:
  -n, --app-name       Display name for the generated Wear OS app (required)
  -p, --package-name   Android package name / applicationId (required)
  -o, --output         Target directory for the generated project (default: derived from app name)
      --force          Overwrite the target directory if it already exists
  -h, --help           Show this message

Examples:
  yarn create-wearos-app --app-name "My Wear App" --package-name com.example.mywear
  yarn create-wearos-app -n "Demo" -p com.example.demo -o ./demo-wear
`);
}

function slugifyAppName(name) {
  return (
    name
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '') || 'wearos-app'
  );
}

function validatePackageName(pkg) {
  return /^[a-zA-Z][\w]*(\.[a-zA-Z][\w]*)+$/.test(pkg ?? '');
}

async function pathExists(targetPath) {
  try {
    await fs.access(targetPath);
    return true;
  } catch (error) {
    return false;
  }
}

async function copyRecursive(src, dest) {
  const stat = await fs.lstat(src);
  if (stat.isSymbolicLink()) {
    const realPath = await fs.readlink(src);
    await fs.symlink(realPath, dest);
    return;
  }

  if (stat.isDirectory()) {
    const directoryName = path.basename(src);
    if (EXCLUDED_DIRECTORY_NAMES.has(directoryName)) {
      return;
    }

    await fs.mkdir(dest, { recursive: true });
    const entries = await fs.readdir(src);
    for (const entry of entries) {
      await copyRecursive(path.join(src, entry), path.join(dest, entry));
    }
    return;
  }

  await fs.mkdir(path.dirname(dest), { recursive: true });
  await fs.copyFile(src, dest, fsConstants.COPYFILE_FICLONE);

  if (path.basename(dest) === 'gradlew') {
    await fs.chmod(dest, 0o755);
  }
}

async function walkFiles(root, callback) {
  const entries = await fs.readdir(root, { withFileTypes: true });
  for (const entry of entries) {
    if (EXCLUDED_DIRECTORY_NAMES.has(entry.name)) {
      continue;
    }

    const fullPath = path.join(root, entry.name);
    if (entry.isDirectory()) {
      await walkFiles(fullPath, callback);
    } else if (entry.isFile()) {
      await callback(fullPath);
    }
  }
}

async function shouldTreatAsText(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  if (BINARY_EXTENSIONS.has(ext)) {
    return false;
  }

  let fileHandle;
  try {
    fileHandle = await fs.open(filePath, 'r');
    const { buffer, bytesRead } = await fileHandle.read({
      buffer: Buffer.alloc(4096),
      position: 0,
    });

    const bytesToInspect = buffer.subarray(0, bytesRead);
    for (let i = 0; i < bytesToInspect.length; i += 1) {
      if (bytesToInspect[i] === 0) {
        return false;
      }
    }
  } catch (error) {
    // If inspection fails, err on the side of skipping replacements to avoid corrupting binaries.
    return false;
  } finally {
    if (fileHandle) {
      await fileHandle.close();
    }
  }

  return true;
}

async function replaceInFile(filePath, replacements) {
  if (!(await shouldTreatAsText(filePath))) {
    return;
  }

  let content = await fs.readFile(filePath, 'utf8');
  let updated = content;
  for (const [searchValue, replacementValue] of replacements) {
    updated = updated.split(searchValue).join(replacementValue);
  }

  if (updated !== content) {
    await fs.writeFile(filePath, updated, 'utf8');
  }
}

async function renamePackageDirectory(projectRoot, newPackageName) {
  const javaSrcRoot = path.join(projectRoot, 'app', 'src', 'main', 'java');
  const oldPackageDir = path.join(javaSrcRoot, 'com', 'wearconnectivityexample');
  const newPackageDir = path.join(javaSrcRoot, ...newPackageName.split('.'));

  if (oldPackageDir === newPackageDir) {
    return;
  }

  if (!(await pathExists(oldPackageDir))) {
    return;
  }

  await fs.mkdir(path.dirname(newPackageDir), { recursive: true });
  await fs.rename(oldPackageDir, newPackageDir);
  await cleanupEmptyDirectories(path.join(javaSrcRoot, 'com'), javaSrcRoot);
}

async function cleanupEmptyDirectories(dir, stopAt) {
  let currentDir = dir;
  while (currentDir.startsWith(stopAt)) {
    const entries = await fs.readdir(currentDir);
    if (entries.length > 0) {
      return;
    }
    await fs.rmdir(currentDir);
    if (currentDir === stopAt) {
      return;
    }
    currentDir = path.dirname(currentDir);
  }
}

async function generateProject({ appName, packageName, output: outputDir, force }) {
  if (!appName || !appName.trim()) {
    throw new Error('Missing required option: --app-name');
  }
  if (!packageName || !validatePackageName(packageName)) {
    throw new Error('Missing or invalid --package-name. Expected format like com.example.app');
  }

  const targetDir = path.resolve(process.cwd(), outputDir ?? slugifyAppName(appName));
  if (await pathExists(targetDir)) {
    if (!force) {
      throw new Error(`Target directory already exists: ${targetDir}. Use --force to overwrite.`);
    }
    await fs.rm(targetDir, { recursive: true, force: true });
  }

  await fs.mkdir(targetDir, { recursive: true });

  for (const entry of TEMPLATE_ENTRIES) {
    const sourcePath = path.join(TEMPLATE_ROOT, entry);
    if (!(await pathExists(sourcePath))) {
      continue;
    }
    await copyRecursive(sourcePath, path.join(targetDir, entry));
  }

  const replacements = [
    ['com.wearconnectivityexample', packageName],
    ['ConnectivityAndroidExample', appName],
  ];

  await walkFiles(targetDir, async (filePath) => {
    await replaceInFile(filePath, replacements);
  });

  await renamePackageDirectory(targetDir, packageName);

  console.log(`\nWear OS project created at: ${targetDir}`);
  console.log(`  App name:     ${appName}`);
  console.log(`  Package name: ${packageName}`);
  console.log('\nNext steps:');
  console.log(`  cd ${path.relative(process.cwd(), targetDir) || '.'}`);
  console.log('  ./gradlew build');
  console.log('\nRemember to update the signing configuration before shipping to production.');
}

async function main() {
  try {
    const options = parseArgs(process.argv);
    if (options.help) {
      printHelp();
      return;
    }
    await generateProject(options);
  } catch (error) {
    console.error(`Error: ${error.message}`);
    process.exitCode = 1;
  }
}

main();
