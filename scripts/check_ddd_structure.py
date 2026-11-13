#!/usr/bin/env python3
"""Structural check for Refrio's eight domain boundaries. No dependencies needed."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'src/main/java/com/refrio/platform'
CONTEXTS = ('iam', 'inventory', 'operations', 'alerting', 'traceability', 'analytics', 'publicportal', 'events')
TESTS = ROOT / 'src/test/java/com/refrio/platform'
folders = {p.name for p in SOURCE.iterdir() if p.is_dir()}
assert folders == {*CONTEXTS, 'shared'}, f'Unexpected or missing module: {folders}'
for context in CONTEXTS:
    base = SOURCE / context
    for section in ('domain', 'application', 'infrastructure', 'interfaces/rest'):
        assert (base / section).is_dir(), f'{context}: missing {section}'
    assert list((base / 'interfaces/rest').rglob('*.java')), f'{context}: missing REST controller'
    assert list((base / 'domain').rglob('*Repository.java')), f'{context}: missing repository port'
    assert list((base / 'infrastructure').rglob('*Adapter.java')), f'{context}: missing persistence adapter'
    assert list((base / 'domain').rglob('*.java')), f'{context}: missing domain model'
    assert list((base / 'domain/model/aggregates').rglob('*.java')), f'{context}: missing aggregate model'
    assert list((base / 'domain/model/repositories').rglob('*.java')), f'{context}: missing repository port'
    assert (TESTS / context).is_dir(), f'{context}: missing test directory'
    assert len(list((TESTS / context).rglob('*Test.java'))) >= 2, f'{context}: expected at least two dedicated test classes'
print(f'DDD STRUCTURE PASSED: {len(CONTEXTS)} bounded contexts, test suites for all, and shared infrastructure')
