<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:key name="funcByName" match="Function" use="@Name"/>
    <xsl:key name="procByName" match="Procedure" use="@Name"/>

    <xsl:template match="/">
        <root>
            <Functions>
                <xsl:for-each select="//Function[generate-id() = generate-id(key('funcByName', @Name)[1])][count(key('funcByName', @Name)) > 1]">
                    <xsl:copy-of select="key('funcByName', @Name)"/>
                </xsl:for-each>
            </Functions>
            <Procedures>
                <xsl:for-each select="//Procedure[generate-id() = generate-id(key('procByName', @Name)[1])][count(key('procByName', @Name)) > 1]">
                    <xsl:copy-of select="key('procByName', @Name)"/>
                </xsl:for-each>
            </Procedures>
        </root>
    </xsl:template>
</xsl:stylesheet>
