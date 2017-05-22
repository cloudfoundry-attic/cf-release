@echo off

set submodule_warning='Uncommitted submodules changes will be clobbered'
set unversioned_change_warning='Unversioned changes will be clobbered'

if "$1" == "--help" (
  echo USAGE: $0
  echo     This command takes no arguments.
  echo.
  echo This script does a git submodule update on all submodules
  echo.
  echo NOTE: $submodule_warning
  goto :end
)

echo.
echo === %submodule_warning% ===

echo.
echo === %unversioned_change_warning% ===

rem show the commands we are running
echo on

@rem only run 'git pull' if upstream has been configured (i.e. not on a tag)
git rev-parse @{u} > nul 2>&1 && git pull

@echo.
@echo # Update submodule pointers; Clean out any submodule changes
git submodule foreach --recursive "git submodule sync; git clean -d --force --force"

@echo.
@echo # Update submodule content, check out if necessary
git submodule update --init --recursive --force

git clean -ffd

@echo.
@echo CF Release has been updated
:end
