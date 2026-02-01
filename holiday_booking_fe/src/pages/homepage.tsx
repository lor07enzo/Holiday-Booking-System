
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { toast } from "sonner";

const API_URL = import.meta.env.VITE_API_URL;

function Homepage() {


    const loadUsers = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/users`);
            if (!res.ok) {
                throw new Error("Failed to fetch users");
            } 
            return res.json();
            
        } catch (error) {
            toast.error("Error server comunication!");
            console.error(error);
        }
        console.log("Users loaded successfully");
    }
    loadUsers();

    const loadHosts = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/hosts`);
            if (!res.ok) {
                throw new Error("Failed to fetch users");
            } 
            return res.json();

        } catch (error) {
            toast.error("Error server comunication!");
            console.error(error);
        }
        console.log("Hosts loaded successfully");
    }
    loadHosts();

    return(
        <div className="w-full h-screen flex flex-col items-center justify-start gap-4">
            <div className="flex flex-col p-4 gap-10 ">

               <div>
                    <h1 className="font-bold text-lg text-center py-2">List of All Users</h1>
                    <div className=" p-2 bg-gray-100 border rounded-lg max-w-6xl w-full">
                        <Table className="w-full">
                            <TableHeader>
                                <TableRow className="grid grid-cols-5">
                                    <TableHead className="w-25">ID</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Last Name</TableHead>
                                    <TableHead>Email</TableHead>
                                    <TableHead className="text-right">Address</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                <TableRow className="grid grid-cols-5">
                                    <TableCell className="font-medium">INV001</TableCell>
                                    <TableCell>Paid</TableCell>
                                    <TableCell>Credit Card</TableCell>
                                    <TableCell>12 Jan 2023</TableCell>
                                    <TableCell className="text-right">$250.00</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </div>
               </div>

                <div>
                    <h1 className="font-bold text-lg text-center py-2">List of All Hosts</h1>
                    <div className=" p-2 bg-gray-100 border rounded-lg max-w-6xl w-full">
                        <Table className="w-full">
                            <TableHeader>
                                <TableRow className="grid grid-cols-5">
                                    <TableHead className="w-25">ID</TableHead>
                                    <TableHead>Host Code</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Last Name</TableHead>
                                    <TableHead className="text-right">Super Host</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                <TableRow className="grid grid-cols-5">
                                    <TableCell className="font-medium">INV001</TableCell>
                                    <TableCell>Paid</TableCell>
                                    <TableCell>Credit Card</TableCell>
                                    <TableCell>12 Jan 2023</TableCell>
                                    <TableCell className="text-right">$250.00</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </div>
                </div>

                {/* <div className="w-full max-w-4xl">
                    <div className="flex items-center w-full border rounded-lg shadow-xl shadow-black/20 px-2">

                        <div className="flex items-center gap-2 py-2 w-full">
                            <Hotel className="shrink-0"/>
                            <div className="flex flex-col min-w-0">
                                <span className="text-xs text-muted-foreground leading-tight">Select your Destination</span>
                                <input
                                    type="text"
                                    placeholder="Insert name of habitation"
                                    className="w-full min-w-0 bg-transparent border-0 p-0 text-sm placeholder:text-muted-foreground focus:outline-none"
                                />
                            </div>
                        </div>

                        <Separator aria-orientation="vertical" className="border mx-2 h-8"/>

                        <Button variant="ghost" className="pl-2 pr-6 h-full">
                            <span className="flex items-center gap-2">
                                <CalendarDays/>
                                <div className="flex flex-col items-start leading-tight">
                                    <span className="text-xs text-muted-foreground">Start / End</span>
                                    <span>DateStart - DateEnd</span>
                                </div>
                            </span>
                        </Button>

                        <Separator aria-orientation="vertical" className="border mx-2 h-8"/>

                        <Button variant="ghost" className="pl-2 pr-6 h-full">
                            <span className="flex items-center gap-2">
                                <Users/>
                                <div className="flex flex-col items-start leading-tight">
                                    <span className="text-xs text-muted-foreground">Guests and Rooms</span>
                                    <span>1 Guest, 1 Room</span>
                                </div>
                            </span>
                        </Button>

                        <Button className="ml-2 h-full px-4">
                            <Search className="mr-2 h-4 w-4"/> Search
                        </Button>
                    </div>
                </div> */}
            </div>
        </div>
    )
}
export default Homepage