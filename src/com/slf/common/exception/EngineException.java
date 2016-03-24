package com.slf.common.exception;

public class EngineException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7028332914954199452L;
	
    public EngineException() {
    }

    /**
     * Constructor that sets the error key
     * 
     * @param msg
     *            Message to report for the exception.
     */
    public EngineException(String msg) {
            super(msg);
    }

    /**
     * Constructor based on an existing exception
     * 
     * @param cause
     *            The root exception to chain.
     */
    public EngineException(Throwable cause) {
            super(cause);
    };

    /**
     * Constructor based on a message and an existing exception.
     * 
     * @param msg
     *            Message to use for the exception
     * @param cause
     *            The root caues to chain
     */
    public EngineException(String msg, Throwable cause) {
            super(msg, cause);
    }

}
