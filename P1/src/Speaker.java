//Name: Yifei Du
public class Speaker implements Runnable{
	private String name;
	private long system_start_time = 0;
	private Monitor mo= null;
	private int totalMovie = 4;
	private int countMovie = 1;

	public Speaker(String name, long time, Monitor mo, int totalMovie) {
	      this.name = name;
	      this.system_start_time = time;
	      this.mo = mo;
	      this.totalMovie = totalMovie;
	      new Thread(this).start();
	}
	
	public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] " + name + ": " + m);
    }
	
	@Override
	public void run() {
		msg("go to the theatre, already for speech!");
		while(countMovie <= totalMovie && mo.theaterIsOpen()) {
			mo.speakerWork();
			countMovie++;
		}
	}

}
