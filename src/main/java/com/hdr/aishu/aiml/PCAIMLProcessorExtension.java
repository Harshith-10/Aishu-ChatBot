// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Set;

public class PCAIMLProcessorExtension implements AIMLProcessorExtension
{
    public Set<String> extensionTagNames;
    
    public PCAIMLProcessorExtension() {
        this.extensionTagNames = Utilities.stringSet("contactid", "multipleids", "displayname", "dialnumber", "emailaddress", "contactbirthday", "addinfo");
    }
    
    @Override
    public Set<String> extensionTagSet() {
        return this.extensionTagNames;
    }
    
    private String newContact(final Node node, final ParseState ps) {
        final NodeList childList = node.getChildNodes();
        String emailAddress = "unknown";
        String displayName = "unknown";
        String dialNumber = "unknown";
        String emailType = "unknown";
        String phoneType = "unknown";
        String birthday = "unknown";
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("birthday")) {
                birthday = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("phonetype")) {
                phoneType = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("emailtype")) {
                emailType = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("dialnumber")) {
                dialNumber = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("displayname")) {
                displayName = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("emailaddress")) {
                emailAddress = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
        }
        System.out.println("Adding new contact " + displayName + " " + phoneType + " " + dialNumber + " " + emailType + " " + emailAddress + " " + birthday);
        final Contact contact = new Contact(displayName, phoneType, dialNumber, emailType, emailAddress, birthday);
        return "";
    }
    
    private String contactId(final Node node, final ParseState ps) {
        final String displayName = AIMLProcessor.evalTagContent(node, ps, null);
        final String result = Contact.contactId(displayName);
        return result;
    }
    
    private String multipleIds(final Node node, final ParseState ps) {
        final String contactName = AIMLProcessor.evalTagContent(node, ps, null);
        final String result = Contact.multipleIds(contactName);
        return result;
    }
    
    private String displayName(final Node node, final ParseState ps) {
        final String id = AIMLProcessor.evalTagContent(node, ps, null);
        final String result = Contact.displayName(id);
        return result;
    }
    
    private String dialNumber(final Node node, final ParseState ps) {
        final NodeList childList = node.getChildNodes();
        String id = "unknown";
        String type = "unknown";
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("id")) {
                id = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("type")) {
                type = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
        }
        final String result = Contact.dialNumber(type, id);
        return result;
    }
    
    private String emailAddress(final Node node, final ParseState ps) {
        final NodeList childList = node.getChildNodes();
        String id = "unknown";
        String type = "unknown";
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("id")) {
                id = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
            if (childList.item(i).getNodeName().equals("type")) {
                type = AIMLProcessor.evalTagContent(childList.item(i), ps, null);
            }
        }
        final String result = Contact.emailAddress(type, id);
        return result;
    }
    
    private String contactBirthday(final Node node, final ParseState ps) {
        final String id = AIMLProcessor.evalTagContent(node, ps, null);
        final String result = Contact.birthday(id);
        return result;
    }
    
    @Override
    public String recursEval(final Node node, final ParseState ps) {
        try {
            final String nodeName = node.getNodeName();
            if (nodeName.equals("contactid")) {
                return this.contactId(node, ps);
            }
            if (nodeName.equals("multipleids")) {
                return this.multipleIds(node, ps);
            }
            if (nodeName.equals("dialnumber")) {
                return this.dialNumber(node, ps);
            }
            if (nodeName.equals("addinfo")) {
                return this.newContact(node, ps);
            }
            if (nodeName.equals("displayname")) {
                return this.displayName(node, ps);
            }
            if (nodeName.equals("emailaddress")) {
                return this.emailAddress(node, ps);
            }
            if (nodeName.equals("contactbirthday")) {
                return this.contactBirthday(node, ps);
            }
            return AIMLProcessor.genericXML(node, ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
