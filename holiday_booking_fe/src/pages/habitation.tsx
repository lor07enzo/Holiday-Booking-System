import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import type { IHost } from "@/types/types";
import { format } from "date-fns";
import { Calendar, CalendarIcon } from "lucide-react";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import z from "zod";


const API_URL = import.meta.env.VITE_API_URL;

const createHabitationFormSchema = z.object({
    hostCode: z.number().min(1, "Host Code is required"),
    name: z.string().min(2, "Name is required"),
    description: z.string().min(2, "Description is required"),
    street: z.string().min(3, "Street is required"),
    city: z.string().min(2, "City is required"),
    country: z.string().min(2, "Country is required"),
    floor: z.number().min(0, "Floor must be at least 0"),
    rooms: z.number().min(1, "There must be at least 1 room"),
    price: z.number().min(1, "Price must be at least 1"),
    startAvailable: z.date(),
    endAvailable: z.date()
}).refine(data => data.endAvailable > data.startAvailable, {
    message: "End date must be after start date",
    path: ["endAvailable"],
});

export type CreateHabitationFormData = z.infer<typeof createHabitationFormSchema>;

export function Habitation() {
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [hosts, setHosts] = useState<IHost[]>([]);
    const [date, setDate] = useState<{ from: Date | undefined; to: Date | undefined }>({
        from: undefined,
        to: undefined
    });
    const [selectedHostCode, setSelectedHostCode] = useState<string>("");

    useEffect(() => {
        const fetchHosts = async () => {
            try {
                const response = await fetch(`${API_URL}/api/v1/hosts`);
                if (!response.ok) {
                    throw new Error("Failed to fetch hosts");
                }
                const data = await response.json();
                setHosts(data);
            } catch (error) {
                console.error("Error fetching hosts:", error);
            }
        };
        fetchHosts();
    }, []);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        const data = {
            hostCode: Number(formData.get("hostCode")),
            name: formData.get("name") as string,
            description: formData.get("description") as string,
            street: formData.get("street") as string,
            city: formData.get("city") as string,
            country: formData.get("country") as string,
            floor: Number(formData.get("floor")),
            rooms: Number(formData.get("rooms")),
            price: Number(formData.get("price")),
            startAvailable: date.from!,
            endAvailable: date.to!
        };

        const parsed = createHabitationFormSchema.safeParse(data);

        if (!parsed.success) {
            const fieldErrors: Record<string, string> = {};
            parsed.error.issues.forEach(issue => {
                fieldErrors[issue.path[0] as string] = issue.message;
            });
            setErrors(fieldErrors);
            return;
        }

        setErrors({});
        const fullAddress = `${parsed.data.street}, ${parsed.data.city}, ${parsed.data.country}`;

        const payload = {
            hostCode: parsed.data.hostCode,
            habitation: {
                name: parsed.data.name,
                description: parsed.data.description,
                address: fullAddress,
                floor: parsed.data.floor,
                rooms: parsed.data.rooms,
                price: parsed.data.price,
                startAvailable: parsed.data.startAvailable.toISOString().split('T')[0],
                endAvailable: parsed.data.endAvailable.toISOString().split('T')[0],
            }
        }

        try {
            const response = await fetch(`${API_URL}/api/v1/habitations`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error("Failed to create habitation");
            }

            e.currentTarget.reset();
            setSelectedHostCode("");
            setDate({ from: undefined, to: undefined });
            toast.success("Habitation created successfully!");
            console.log("Habitation created successfully!");

        } catch (error) {
            toast.error("Error creating habitation!");
            console.error("Error creating habitation:", error);
        }
    }

    return (
        <div className=" w-full flex flex-col h-screen justify-center items-center">
            <Card className="w-full max-w-4xl">
                <CardHeader>
                    <CardTitle>Create a New Habitation</CardTitle>
                    <CardDescription>
                        Enter your information below to create your habitation.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <FieldGroup >
                            <div className="grid grid-cols-2 gap-4">
                                <Field >
                                    <FieldLabel htmlFor="hostCode">Host Code</FieldLabel>
                                    <Select name="hostCode" value={selectedHostCode} onValueChange={setSelectedHostCode}>
                                        <SelectTrigger className="w-full " aria-invalid={!!errors.hostCode}>
                                            <SelectValue placeholder="Select a Host" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Hosts</SelectLabel>
                                                {hosts.length === 0 ? ( 
                                                    <SelectItem value="null">No hosts Found</SelectItem> 
                                                ) : (
                                                    hosts.map(host => (
                                                        <SelectItem key={host.hostCode} value={String(host.hostCode)}>
                                                            Host - {host.hostCode}
                                                        </SelectItem>
                                                    ))
                                                )}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                    {errors.hostCode && <p className="text-red-500 text-sm">{errors.hostCode}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="name">Name</FieldLabel>
                                    <Input id="name" name="name" placeholder="Habitation in Via Roma 56" aria-invalid={!!errors.name} />
                                    {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="description">Description</FieldLabel>
                                    <Input 
                                        id="description" 
                                        name="description" 
                                        placeholder="Description of the house" 
                                        aria-invalid={!!errors.description} />
                                    {errors.description && <p className="text-red-500 text-sm">{errors.description}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="street">Street</FieldLabel>
                                    <Input id="street" name="street" placeholder="Via Roma, 56" aria-invalid={!!errors.street} />
                                    {errors.street && <p className="text-red-500 text-sm">{errors.street}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="city">City</FieldLabel>
                                    <Input id="city" name="city" placeholder="Firenze" aria-invalid={!!errors.city} />
                                    {errors.city && <p className="text-red-500 text-sm">{errors.city}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="country">Country</FieldLabel>
                                    <Input id="country" name="country" placeholder="Italy" aria-invalid={!!errors.country} />
                                    {errors.country && <p className="text-red-500 text-sm">{errors.country}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="floor">Number of Floors</FieldLabel>
                                    <Input id="floor" type="number" name="floor" placeholder="ex. 1" aria-invalid={!!errors.floor} />
                                    {errors.floor && <p className="text-red-500 text-sm">{errors.floor}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="rooms">Number of Rooms</FieldLabel>
                                    <Input id="rooms" type="number" name="rooms" placeholder="ex. 2" aria-invalid={!!errors.rooms} />
                                    {errors.rooms && <p className="text-red-500 text-sm">{errors.rooms}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel htmlFor="price">Price for Night</FieldLabel>
                                    <Input id="price" type="number" name="price" placeholder="€50" aria-invalid={!!errors.price} />
                                    {errors.price && <p className="text-red-500 text-sm">{errors.price}</p>}
                                </Field>

                                <Field>
                                    <FieldLabel >Select the Availability Period</FieldLabel>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant="outline"
                                                id="date-picker-range"
                                                className="justify-start px-2.5 font-normal"
                                            >
                                                <CalendarIcon />
                                                {date?.from ? (
                                                    date.to ? (
                                                        <>
                                                            {format(date.from, "yyyy-MM-dd")} -{" "}
                                                            {format(date.to, "yyyy-MM-dd")}
                                                        </>
                                                    ) : (
                                                        format(date.from, "yyyy-MM-dd")
                                                    )
                                                ) : (
                                                    <span>Pick a date</span>
                                                )}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0" align="start">
                                            <Calendar
                                                mode="range"
                                                defaultMonth={date?.from}
                                                selected={date}
                                                onSelect={setDate}
                                                numberOfMonths={2}
                                            />
                                        </PopoverContent>
                                    </Popover>
                                    {errors.startAvailable && <p className="text-red-500 text-sm">{errors.startAvailable}</p>}
                                    {errors.endAvailable && <p className="text-red-500 text-sm">{errors.endAvailable}</p>}
                                </Field>
                            </div>

                            <Button type="submit">Add Habitation</Button>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}