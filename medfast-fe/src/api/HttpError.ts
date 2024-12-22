export class HttpError extends Error {
    public status: number;
    public errorMessage: string;
  
    constructor(status: number, errorMessage: string) {
      super(status.toString());
      this.status = status;
      this.errorMessage = errorMessage;
    }
  }
  