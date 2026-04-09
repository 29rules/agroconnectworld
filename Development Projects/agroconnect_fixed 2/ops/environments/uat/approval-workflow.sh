#!/bin/bash
# UAT Approval Workflow Script
# Manages internal approvals for UAT deployments

set -e

APPROVAL_FILE="/tmp/uat_approval_status.json"
REQUIRED_APPROVERS=("qa-lead" "ceo" "cto")

echo "=== UAT Approval Workflow ==="
echo ""

# Initialize approval status
initialize_approvals() {
    cat > "$APPROVAL_FILE" <<EOF
{
  "status": "pending",
  "approvals": {},
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
}
EOF
}

# Check if all approvers have approved
check_approvals() {
    local all_approved=true
    for approver in "${REQUIRED_APPROVERS[@]}"; do
        if ! grep -q "\"$approver\": \"approved\"" "$APPROVAL_FILE"; then
            all_approved=false
            break
        fi
    done
    
    if [ "$all_approved" = true ]; then
        echo "✅ All approvals received. UAT deployment approved."
        jq '.status = "approved"' "$APPROVAL_FILE" > "${APPROVAL_FILE}.tmp" && mv "${APPROVAL_FILE}.tmp" "$APPROVAL_FILE"
        return 0
    else
        echo "⏳ Waiting for approvals..."
        return 1
    fi
}

# Record approval
record_approval() {
    local approver=$1
    echo "Recording approval from: $approver"
    jq ".approvals.\"$approver\" = \"approved\" | .approvals.\"${approver}_timestamp\" = \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\"" "$APPROVAL_FILE" > "${APPROVAL_FILE}.tmp" && mv "${APPROVAL_FILE}.tmp" "$APPROVAL_FILE"
}

# Main workflow
main() {
    case "${1:-check}" in
        init)
            initialize_approvals
            echo "Approval workflow initialized. Required approvers: ${REQUIRED_APPROVERS[*]}"
            ;;
        approve)
            if [ -z "$2" ]; then
                echo "Usage: $0 approve <approver-name>"
                exit 1
            fi
            record_approval "$2"
            check_approvals
            ;;
        check)
            if [ ! -f "$APPROVAL_FILE" ]; then
                initialize_approvals
            fi
            check_approvals
            ;;
        status)
            if [ -f "$APPROVAL_FILE" ]; then
                cat "$APPROVAL_FILE" | jq .
            else
                echo "No approval file found. Run 'init' first."
            fi
            ;;
        *)
            echo "Usage: $0 {init|approve|check|status}"
            exit 1
            ;;
    esac
}

main "$@"



