package utils;

import models.UpstreamMessage;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import xmpp.GcmPacketExtension;
import xmpp.MessageHelper;

import java.util.Map;

public class MessageHandler {

    private XMPPTCPConnection mConnection;

    public MessageHandler(XMPPTCPConnection xmpptcpConnection) {
        this.mConnection = xmpptcpConnection;
    }

    public void handleUpstreamMessage(UpstreamMessage message) {
        final String action = message.getDataPayload().get("ACTION");
        final String msg = message.getDataPayload().get("MESSAGE");

        sendDownstreamMessage(MessageHelper.createJsonAck(message.getFrom(), message.getMessageId()));

        System.out.println("Received: " + action + " with message: " + msg);
    }

    public void handleAckReceipt(Map<String, Object> jsonMap) {
        // TODO
    }

    public void handleNackReceipt(Map<String, Object> jsonMap) {
        String errorCode = (String) jsonMap.get("error");

        if (errorCode == null) {
            return;
        }

        switch (errorCode) {
            case "INVALID_JSON":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "BAD_REGISTRATION":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "DEVICE_UNREGISTERED":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "BAD_ACK":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "SERVICE_UNAVAILABLE":
                handleServerFailure(jsonMap);
                break;
            case "INTERNAL_SERVER_ERROR":
                handleServerFailure(jsonMap);
                break;
            case "DEVICE_MESSAGE_RATE_EXCEEDED":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "TOPICS_MESSAGE_RATE_EXCEEDED":
                handleUnrecoverableFailure(jsonMap);
                break;
            case "CONNECTION_DRAINING":
                handleConnectionDrainingFailure();
                break;
            default:
        }
    }

    public void sendDownstreamMessage(String jsonRequest) {
        Stanza request = new GcmPacketExtension(jsonRequest).toPacket();
        try {
            mConnection.sendStanza(request);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void handleDeliveryReceipt(Map<String, Object> jsonMap) {
        // TODO: handle the delivery receipt
    }

    public void handleControlMessage(Map<String, Object> jsonMap) {
        // TODO: handle the control message
        String controlType = (String) jsonMap.get("control_type");

        if (controlType.equals("CONNECTION_DRAINING")) {
            handleConnectionDrainingFailure();
        }
    }

    public void handleServerFailure(Map<String, Object> jsonMap) {
        // TODO: Resend message
    }

    public void handleUnrecoverableFailure(Map<String, Object> jsonMap) {
        // TODO
    }

    public void handleConnectionDrainingFailure() {
        // TODO
    }
}
