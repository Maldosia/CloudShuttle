package com.maldosia.mobile.protocol;

import com.maldosia.cloudshuttle.core.AbstractProtocol;
import io.netty.channel.ChannelHandler;

/**
 *
 * 0     1     2     3     4     5     6     7     8     9     10     11    12    13    14    15    16
 * +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+------+-----+-----+-----+-----+
 * |      start flag       |      check code       |        version         |     function code     |
 * +-----------------------+------------------------------------------------------------------------+
 * |    content length     |                              reserve                                   |
 * +------------------------------------------------------------------------------------------------+
 * |                                      content                                                   |
 * +------------------------------------------------------------------------------------------------+
 *
 * @author Maldosia
 * @since 2025/6/26
 */
public class MobileProtocol extends AbstractProtocol {


    @Override
    public ChannelHandler getDecoder() {

        return null;
    }
}