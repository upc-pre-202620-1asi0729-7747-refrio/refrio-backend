#!/usr/bin/env python3
"""Compile all business domain classes and run eight-context domain smoke test with Java 21.

No Maven/Gradle/external dependencies are required. Does not replace `mvn verify`.
"""
from pathlib import Path
import os
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
SOURCES = ROOT / 'src/main/java/com/refrio/platform'
SMOKE = ROOT / 'scripts/domain-check/DomainSmoke.java'
java_files = sorted(p for p in SOURCES.rglob('*.java') if 'domain' in p.relative_to(SOURCES).parts)
assert SMOKE.is_file(), 'DomainSmoke.java missing'
with tempfile.TemporaryDirectory(prefix='refrio-domain-') as folder:
    classes = Path(folder)/'classes'
    classes.mkdir()
    subprocess.run(['javac', '-encoding', 'UTF-8', '-d', str(classes),
                    *map(str, java_files), str(SMOKE)], check=True)
    subprocess.run(['java', '-cp', str(classes), 'DomainSmoke'], check=True)
    print(f'Compiled {len(java_files)} domain Java files with javac.')
