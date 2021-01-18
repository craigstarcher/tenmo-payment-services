package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfers;

public interface TransfersDAO {
	
	Transfers transferRequest(Transfers transfer);
	boolean updateTransferStatus(Transfers transfer);
	List<Transfers> history(int account_id);
	List<Transfers> pending(int account_id);
	boolean sendTransfer(Transfers transfer);
	boolean updateBalanceFromTransfer(Transfers transfer);
	Transfers transferDetails(int transferId);
	String getTransferStatusName(int transferStatusId);
	String getTransferTypeName(int transferTypeId);
}
