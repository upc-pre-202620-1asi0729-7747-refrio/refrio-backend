#!/usr/bin/env python3
"""Deterministic source checks that do not require downloaded Maven dependencies.
Not a replacement for compiler/JUnit/HTTP integration checks.
"""
from pathlib import Path
import re
ROOT=Path(__file__).resolve().parents[1]
PACKAGE=ROOT/'src/main/java'
TEST_PACKAGE=ROOT/'src/test/java'
errors=[]
files=list(PACKAGE.rglob('*.java'))+list(TEST_PACKAGE.rglob('*.java'))
for f in files:
    s=f.read_text(encoding='utf-8')
    match=re.search(r'^package\s+([\w.]+);',s,re.MULTILINE)
    expected='.'.join(f.relative_to(PACKAGE if PACKAGE in f.parents else TEST_PACKAGE).parent.parts)
    if not match or match.group(1)!=expected:
        errors.append(f'{f}: declared package differs from path ({expected})')
    for imp in re.findall(r'^import\s+(?!static)(com\.refrio\.platform\.[\w.]+);',s,re.MULTILINE):
        imported=PACKAGE.joinpath(*imp.split('.')).with_suffix('.java')
        if not imported.exists():
            # Nested types have an enclosing type in the package.
            prefix=PACKAGE.joinpath(*imp.split('.')[:-1]).with_suffix('.java')
            if not prefix.exists():errors.append(f'{f}: unresolved local import {imp}')
    if '/domain/' in str(f.relative_to(ROOT)) and ('import org.springframework' in s or 'import com.fasterxml.jackson' in s or 'import jakarta.persistence' in s):
        errors.append(f'{f}: Spring/Jackson/JPA dependence inside domain')
for doc in (ROOT/'docs').glob('*.md'):
    s=doc.read_text()
    for destination in re.findall(r'\]\(([^)]+)\)',s):
        if not destination.startswith(('http:','https:','#','mailto:')) and not (doc.parent/destination).exists():
            errors.append(f'{doc}: broken doc link {destination}')
if errors:
    print('\n'.join(errors));raise SystemExit(f'FAILED: {len(errors)} issues')
print(f'SOURCE INTEGRITY PASSED: {len(files)} Java files, imports, package names and domain independence')
