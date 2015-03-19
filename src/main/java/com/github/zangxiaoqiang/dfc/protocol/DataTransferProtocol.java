package com.github.zangxiaoqiang.dfc.protocol;

import java.io.DataInputStream;
import java.io.IOException;

public interface DataTransferProtocol {

	public static final short DATA_TRANSFER_VERSION = 1;
	public static final long DEFAULT_BLOCK_SIZE = 64*1024*1024;

	/** Receiver */
	public static abstract class Receiver {
		/** Read an Op. It also checks protocol version. */
		protected final Op readOp(DataInputStream in) throws IOException {
			final short version = in.readShort();
			if (version != DATA_TRANSFER_VERSION) {
				throw new IOException("Version Mismatch (Expected: "
						+ DataTransferProtocol.DATA_TRANSFER_VERSION
						+ ", Received: " + version + " )");
			}
			// return Op.read(in);
			return Op.valueOf(in.readByte());
		}

		/** Process op by the corresponding method. */
		protected final void processOp(Op op, DataInputStream in)
				throws IOException {
			if (op == null) {
				return;
			}

			switch (op) {
			case READ_BLOCK:
				opReadBlock(in);
				break;
			case WRITE_BLOCK:
				opWriteBlock(in);
				break;
			case WRITE_FILE:
				opWriteFile(in);
				break;
			case READ_FILE:
				opReadFile(in);
				break;
			default:
				throw new IOException("Unknown op " + op + " in data stream");
			}
		}

		protected abstract void opReadFile(DataInputStream in);

		protected abstract void opWriteFile(DataInputStream in);

		/** Receive OP_READ_BLOCK */
		private void opReadBlock(DataInputStream in) throws IOException {
			// final long blockId = in.readLong();
			// final long blockGs = in.readLong();
			// final long offset = in.readLong();
			// final long length = in.readLong();
			// final String client = Text.readString(in);
			// final BlockAccessToken accesstoken = readAccessToken(in);
			//
			// opReadBlock(in, blockId, blockGs, offset, length, client,
			// accesstoken);
		}

		/**
		 * Abstract OP_READ_BLOCK method. Read a block.
		 */
		protected abstract void opReadBlock(DataInputStream in, long blockId,
				long blockGs, long offset, long length, String client)
				throws IOException;

		/** Receive OP_WRITE_BLOCK */
		private void opWriteBlock(DataInputStream in) throws IOException {
			// final long blockId = in.readLong();
			// final long blockGs = in.readLong();
			// final int pipelineSize = in.readInt(); // num of datanodes in
			// entire
			// // pipeline
			// final BlockConstructionStage stage = BlockConstructionStage
			// .readFields(in);
			// final long newGs = WritableUtils.readVLong(in);
			// final long minBytesRcvd = WritableUtils.readVLong(in);
			// final long maxBytesRcvd = WritableUtils.readVLong(in);
			// final String client = Text.readString(in); // working on behalf
			// of
			// // this client
			// final DatanodeInfo src = in.readBoolean() ? DatanodeInfo.read(in)
			// : null;
			//
			// final int nTargets = in.readInt();
			// if (nTargets < 0) {
			// throw new IOException("Mislabelled incoming datastream.");
			// }
			// final DatanodeInfo targets[] = new DatanodeInfo[nTargets];
			// for (int i = 0; i < targets.length; i++) {
			// targets[i] = DatanodeInfo.read(in);
			// }
			// final BlockAccessToken accesstoken = readAccessToken(in);
			//
			// opWriteBlock(in, blockId, blockGs, pipelineSize, stage, newGs,
			// minBytesRcvd, maxBytesRcvd, client, src, targets,
			// accesstoken);
		}

		/**
		 * Abstract OP_WRITE_BLOCK method. Write a block.
		 */
		// protected abstract void opWriteBlock(DataInputStream in, long
		// blockId,
		// long blockGs, int pipelineSize, BlockConstructionStage stage,
		// long newGs, long minBytesRcvd, long maxBytesRcvd,
		// String client, DatanodeInfo src, DatanodeInfo[] targets,
		// BlockAccessToken accesstoken) throws IOException;

		// /** Receive OP_REPLACE_BLOCK */
		// private void opReplaceBlock(DataInputStream in) throws IOException {
		// final long blockId = in.readLong();
		// final long blockGs = in.readLong();
		// final String sourceId = Text.readString(in); // read del hint
		// final DatanodeInfo src = DatanodeInfo.read(in); // read proxy source
		// final BlockAccessToken accesstoken = readAccessToken(in);
		//
		// opReplaceBlock(in, blockId, blockGs, sourceId, src, accesstoken);
		// }

		/**
		 * Abstract OP_REPLACE_BLOCK method. It is used for balancing purpose;
		 * send to a destination
		 */
		// protected abstract void opReplaceBlock(DataInputStream in,
		// long blockId, long blockGs, String sourceId, DatanodeInfo src,
		// BlockAccessToken accesstoken) throws IOException;
		//
		// /** Receive OP_COPY_BLOCK */
		// private void opCopyBlock(DataInputStream in) throws IOException {
		// final long blockId = in.readLong();
		// final long blockGs = in.readLong();
		// final BlockAccessToken accesstoken = readAccessToken(in);
		//
		// opCopyBlock(in, blockId, blockGs, accesstoken);
		// }

		/**
		 * Abstract OP_COPY_BLOCK method. It is used for balancing purpose; send
		 * to a proxy source.
		 */
		// protected abstract void opCopyBlock(DataInputStream in, long blockId,
		// long blockGs, BlockAccessToken accesstoken) throws IOException;
		//
		// /** Receive OP_BLOCK_CHECKSUM */
		// private void opBlockChecksum(DataInputStream in) throws IOException {
		// final long blockId = in.readLong();
		// final long blockGs = in.readLong();
		// final BlockAccessToken accesstoken = readAccessToken(in);
		//
		// opBlockChecksum(in, blockId, blockGs, accesstoken);
		// }

		/**
		 * Abstract OP_BLOCK_CHECKSUM method. Get the checksum of a block
		 */
		// protected abstract void opBlockChecksum(DataInputStream in,
		// long blockId, long blockGs, BlockAccessToken accesstoken)
		// throws IOException;
		//
		// /** Read an AccessToken */
		// static private BlockAccessToken readAccessToken(DataInputStream in)
		// throws IOException {
		// final BlockAccessToken t = new BlockAccessToken();
		// t.readFields(in);
		// return t;
		// }
	}
}
