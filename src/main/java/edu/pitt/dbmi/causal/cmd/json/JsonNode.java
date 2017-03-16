/*
 * Copyright (C) 2017 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.causal.cmd.json;

/**
 * Author : Jeremy Espino MD Created 6/6/16 4:51 PM
 */
//// {"name":"foo","nodes":[{"name":"Node889"},{"name":"Node9728"}],"edgeSets":[{"name":"fooEdgeSet0","edges":[{"source":1,"target":0,"etype":"UNK"}]}]}
public class JsonNode {

    public JsonNode(String name) {
        this.name = name;
    }

    public String name;

}
