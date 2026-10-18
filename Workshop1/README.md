# Workshop 1

## How to Run
```bash
cd <path/to/this/repo>/Workshop1
make
```
## Installing Make on Windows

Windows doesn't include `make` by default. Pick **one** of the options below.

### Option 1: Chocolatey

1. Install [Chocolatey](https://chocolatey.org/install) if you don't have it.
2. In an **administrator** PowerShell window, run:
```powershell
   choco install make
```

### Option 2: Scoop

1. Install [Scoop](https://scoop.sh) if you don't have it.
2. Run:
```powershell
   scoop install make
```

### Option 3: winget

```powershell
winget install ezwinports.make
```

### Option 4: MSYS2

1. Install [MSYS2](https://www.msys2.org).
2. Open the **MSYS2 shell** and run:
```bash
   pacman -S make
```
3. Add `C:\msys64\usr\bin` to your `PATH` if you want to run `make` outside the MSYS2 shell.

### Option 5: WSL (Windows Subsystem for Linux)

1. In an administrator PowerShell window, run:
```powershell
   wsl --install
```
2. Restart, then open the Linux distro and run:
```bash
   sudo apt update && sudo apt install make
```

### Verify the installation

Open a **new** terminal and run:

```bash
make --version
```

You should see output like `GNU Make 4.x`.

### Notes

- You also need a JDK installed and on your `PATH` (`java -version` to check).
- This project's Makefile uses Unix-style commands (`./gradlew`, `@echo`). For best results, run `make` from **Git Bash**, **MSYS2**, or **WSL**. If you run it from `cmd` or PowerShell, use `gradlew.bat run` instead.
