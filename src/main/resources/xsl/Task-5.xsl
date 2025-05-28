<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:key name="paramByName" match="Param" use="@Name"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Param[count(key('paramByName', @Name)) > 1]">
        <xsl:copy>
            <xsl:attribute name="ID"><xsl:value-of select="@ID"/></xsl:attribute>
            <xsl:attribute name="Name">
                <xsl:value-of select="concat(@Name, @pos)"/>
            </xsl:attribute>
            <xsl:for-each select="@*">
                <xsl:if test="name() != 'ID' and name() != 'Name'">
                    <xsl:copy-of select="."/>
                </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>