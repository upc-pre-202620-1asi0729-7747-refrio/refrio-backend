#!/usr/bin/env python3
"""Compare our legacy server command contract against an existing Angular checkout.
Usage: python scripts/check_frontend_contract.py ../refrio-frontend
This does NOT execute an end-to-end HTTP integration test.
"""
import pathlib,re,sys
server=pathlib.Path(__file__).resolve().parents[1]
front=pathlib.Path(sys.argv[1]).resolve() if len(sys.argv)>1 else None
if front is None or not (front/'src/app/operations/application/operations.store.ts').is_file():
    sys.exit('Usage: python scripts/check_frontend_contract.py PATH_TO_REFIO_FRONTEND')
store=(front/'src/app/operations/application/operations.store.ts').read_text()
api=(front/'src/app/operations/application/operations-api.service.ts').read_text()
impl=(server/'src/main/java/com/refrio/platform/operations/application/OperationsApplicationService.java').read_text()
expected={name for name in re.findall(r"\.execute<[^>]*>\('([^']+)'",store)}
implemented={name for name in re.findall(r'case "([a-zA-Z]+)"\s*->',impl)}
missing=sorted(expected-implemented)
assert '/operations' in api and '/'+"'" not in api  # original frontend uses environment base path + /operations
assert not missing,f'Missing backend commands: {missing}'
print('PASS: all frontend operations commands are implemented:',', '.join(sorted(expected)))
print('NOTE: static contract check only; run app integration tests after deploying API.')
